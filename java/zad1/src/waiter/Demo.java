package waiter;

import base.AbstractDemo;
import base.IFork;
import base.AbstractPhilosopher;

public class Demo extends AbstractDemo {
    private final Waiter waiter;

    public Demo(int numberOfPhilosophers, int numberOfMeals, boolean commonPoolOfMeals) {
        super(numberOfPhilosophers, numberOfMeals, commonPoolOfMeals);
        waiter = new Waiter(numberOfPhilosophers);
    }

    @Override
    protected IFork makeFork() {
        return new Fork();
    }

    @Override
    protected AbstractPhilosopher makePhilosopher(int i) {
        return new Philosopher(i+1, forks[i], forks[(i+1)%numberOfPhilosophers], waiter, mealsLeftCounter[i]);
    }
}
