package asymmetric;

import base.AbstractDemo;
import base.IFork;
import base.AbstractPhilosopher;

public class Demo extends AbstractDemo {
    public Demo(int numberOfPhilosophers, int numberOfMeals, boolean commonPoolOfMeals) {
        super(numberOfPhilosophers, numberOfMeals, commonPoolOfMeals);
    }

    @Override
    protected IFork makeFork() {
        return new Fork();
    }

    @Override
    protected AbstractPhilosopher makePhilosopher(int i) {
        return new Philosopher(i+1, forks[i], forks[(i+1)%numberOfPhilosophers], mealsLeftCounter[i]);
    }
}
