package base;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class AbstractDemo {
    protected final int numberOfPhilosophers;
    protected final int numberOfMeals;
    protected IFork[] forks;
    protected AbstractPhilosopher[] philosophers;
    protected final AtomicInteger[] mealsLeftCounter;

    public AbstractDemo(int numberOfPhilosophers, int numberOfMeals, boolean commonPoolOfMeals) {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.numberOfMeals = numberOfMeals;
        this.forks = new IFork[numberOfPhilosophers];
        this.philosophers = new AbstractPhilosopher[numberOfPhilosophers];
        this.mealsLeftCounter = new AtomicInteger[numberOfPhilosophers];

        if (commonPoolOfMeals) {
            // common pool of meals
            AtomicInteger commonMealsLeftCounter = new AtomicInteger(numberOfPhilosophers * numberOfMeals);
            for (int i = 0; i < numberOfPhilosophers; ++i) {
                this.mealsLeftCounter[i] = commonMealsLeftCounter;
            }
        } else {
            // individual pool of meals
            for (int i = 0; i < numberOfPhilosophers; ++i) {
                this.mealsLeftCounter[i] = new AtomicInteger(numberOfMeals);
            }
        }
    }

    public void run(String resultsPath) throws FileNotFoundException {
        forks = new IFork[numberOfPhilosophers];
        for (int i = 0; i < numberOfPhilosophers; ++i) {
            forks[i] = makeFork();
        }

        for (int i = 0; i < numberOfPhilosophers; ++i) {
            philosophers[i] = makePhilosopher(i);
            philosophers[i].start();
        }

        for (int i = 0; i < numberOfPhilosophers; ++i) {
            try {
                philosophers[i].join();
            } catch (InterruptedException __) {}
        }

        try (PrintStream out = new PrintStream(new FileOutputStream(resultsPath))) {
            for (int i = 0; i < numberOfPhilosophers; ++i) {
                philosophers[i].printMeasurements(out);
            }
        }
    }

    abstract protected IFork makeFork();

    abstract protected AbstractPhilosopher makePhilosopher(int i);
}
