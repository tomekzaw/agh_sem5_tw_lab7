package naive;

import base.AbstractDemo;

public class Demo extends AbstractDemo {
    public Demo(int numberOfPhilosophers, int numberOfMeals, boolean commonPoolOfMeals) {
        super(numberOfPhilosophers, numberOfMeals, commonPoolOfMeals);
    }

    @Override
    protected Fork makeFork() {
        return new Fork();
    }

    @Override
    protected Philosopher makePhilosopher(int i) {
        return new Philosopher(i+1, forks[i], forks[(i+1)%numberOfPhilosophers], mealsLeftCounter[i]);
    }
}
