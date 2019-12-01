CC=gcc
CFLAGS=-Wall -Wextra

all: naive simultaneous asymmetric waiter

naive:
	$(CC) $(CFLAGS) -o naive main.c -DMODE_NAIVE -pthread

simultaneous:
	$(CC) $(CFLAGS) -o simultaneous main.c -DMODE_SIMULTANEOUS -pthread

asymmetric:
	$(CC) $(CFLAGS) -o asymmetric main.c -DMODE_ASYMMETRIC -pthread

waiter:
	$(CC) $(CFLAGS) -o waiter main.c -DMODE_WAITER -pthread

test: all
	./simultaneous > results/c_simultaneous.log
	./asymmetric > results/c_asymmetric.log
	./waiter > results/c_waiter.log

clean:
	rm -f *.o naive simultaneous asymmetric waiter
