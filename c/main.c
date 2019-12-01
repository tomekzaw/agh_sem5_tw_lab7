#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/time.h>
#include <semaphore.h>

#if defined(MODE_NAIVE)
#elif defined(MODE_ASYMMETRIC)
#elif defined(MODE_SIMULTANEOUS)
#elif defined(MODE_WAITER)
#else
    #error "You must use either -DMODE_NAIVE, -DMODE_ASYMMETRIC, -DMODE_SIMULTANEOUS, or -DMODE_WAITER to compile"
#endif

#define EATING_TIME_US 10000 // microseconds
#define SLEEPING_TIME_US 10000 // microseconds

const unsigned N = 5; // number of philosophers
const unsigned M = 1000; // number of meals for each philosopher

typedef struct philosopher_t {
    pthread_t pid;
    unsigned i;
    unsigned id;
    unsigned left;
    unsigned right;
    unsigned long *measurements;
} philosopher_t;

philosopher_t *philosophers = NULL;

#if defined(MODE_NAIVE) || defined(MODE_ASYMMETRIC) || defined(MODE_WAITER)
    sem_t *forks = NULL;
#endif
#ifdef MODE_SIMULTANEOUS
    bool *taken = NULL;
    pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;
    pthread_cond_t condition = PTHREAD_COND_INITIALIZER;
#endif
#ifdef MODE_WAITER
    sem_t waiter;
#endif

pthread_mutex_t print_mutex = PTHREAD_MUTEX_INITIALIZER;

inline static unsigned long timeval_diff_usec(const struct timeval *start, const struct timeval *end) { // calculate time difference in microseconds
    return (end->tv_sec - start->tv_sec) * 1e6L + (end->tv_usec - start->tv_usec);
}

inline static void print_times(philosopher_t *philosopher) {
    pthread_mutex_lock(&print_mutex);
    for (unsigned m = 0; m < M; ++m) {
        printf("%lu;", philosopher->measurements[m] * 1000); // convert ms to us
    }
    printf("\n");
    pthread_mutex_unlock(&print_mutex);
}

void *philosopher_routine(void *arg) {
    philosopher_t *philosopher = (philosopher_t*) arg;
    struct timeval time_start, time_end;
    
    #if defined(MODE_NAIVE) || defined(MODE_ASYMMETRIC) || defined(MODE_WAITER)
        sem_t *left = &forks[philosopher->left], *right = &forks[philosopher->right];
    #endif
    #ifdef MODE_SIMULTANEOUS
        bool *left = &taken[philosopher->left], *right = &taken[philosopher->right];
    #endif
    #ifdef MODE_ASYMMETRIC
        sem_t *first, *second;
        if (philosopher->id % 2) {
            first = left, second = right;
        } else {
            first = right, second = left;
        }
    #endif

    for (unsigned m = 0; m < M; ++m) {
        gettimeofday(&time_start, NULL);

        #ifdef MODE_NAIVE
            sem_wait(left);
            sem_wait(right);
        #endif
        #ifdef MODE_ASYMMETRIC
            sem_wait(first);
            sem_wait(second);
        #endif
        #ifdef MODE_SIMULTANEOUS
            pthread_mutex_lock(&lock);
            do {
                if (!*left && !*right) {
                    *left = *right = true;
                    break;
                } else {
                    pthread_cond_wait(&condition, &lock);
                }
            } while (1);
        #endif
        #ifdef MODE_WAITER
            sem_wait(&waiter);
            sem_wait(left);
            sem_wait(right);
        #endif

        gettimeofday(&time_end, NULL);
        philosopher->measurements[m] = timeval_diff_usec(&time_start, &time_end);
            
        // eating
        fprintf(stderr, "%d %d\n", philosopher->id, m);
        usleep(rand() % EATING_TIME_US);

        #ifdef MODE_NAIVE
            sem_post(left);
            sem_post(right);
        #endif
        #ifdef MODE_ASYMMETRIC
            sem_post(first);
            sem_post(second);
        #endif
        #ifdef MODE_SIMULTANEOUS
            *left = *right = false;
		    pthread_mutex_unlock(&lock);
        #endif
        #ifdef MODE_WAITER
            sem_post(left);
            sem_post(right);
            sem_post(&waiter);
        #endif
        
        // sleeping
        usleep(rand() % SLEEPING_TIME_US);
    }

    print_times(philosopher);
    return NULL;
}

int main() {
    philosophers = malloc(N*sizeof(philosopher_t));

    #if defined(MODE_NAIVE) || defined(MODE_ASYMMETRIC) || defined(MODE_WAITER)
        forks = malloc(N*sizeof(pthread_mutex_t));
        for (unsigned i = 0; i < N; ++i) {        
            sem_init(&forks[i], 0, 1);
        }
    #endif
    #ifdef MODE_SIMULTANEOUS
        taken = malloc(N*sizeof(bool));
        for (unsigned i = 0; i < N; ++i) {
            taken[i] = false;
        }
    #endif
    #ifdef MODE_WAITER
        sem_init(&waiter, 0, N-1);
    #endif

    for (unsigned i = 0; i < N; ++i) {
        philosopher_t *philosopher = &philosophers[i];
        philosopher->i = i;
        philosopher->id = i+1;
        philosopher->left = i;
        philosopher->right = (i+1)%N;
        philosopher->measurements = malloc(M*sizeof(unsigned long));
        if (pthread_create(&philosopher->pid, NULL, *philosopher_routine, philosopher) != 0) {
            fprintf(stderr, "Cannot launch philosopher #%d\n", philosopher->id);
            exit(EXIT_FAILURE);
        }
    }

    for (unsigned i = 0; i < N; ++i) {
        philosopher_t *philosopher = &philosophers[i];
        pthread_join(philosopher->pid, NULL);
        free(philosopher->measurements);
    }

    #ifdef MODE_WAITER
        sem_destroy(&waiter);
    #endif
    #ifdef MODE_SIMULTANEOUS
        free(taken);
    #endif
    #if defined(MODE_NAIVE) || defined(MODE_ASYMMETRIC) || defined(MODE_WAITER)
        for (unsigned i = 0; i < N; ++i) {
            sem_destroy(&forks[i]);
        }
        free(forks);
    #endif

    free(philosophers);

    return EXIT_SUCCESS; // to avoid Wreturn-type
}
