package simultaneous;

import base.AbstractDemo;
import base.IFork;
import base.AbstractPhilosopher;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo extends AbstractDemo {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Demo(int numberOfPhilosophers, int numberOfMeals, boolean commonPoolOfMeals) {
        super(numberOfPhilosophers, numberOfMeals, commonPoolOfMeals);
    }

    @Override
    public IFork makeFork() {
        return new Fork();
    }

    @Override
    protected AbstractPhilosopher makePhilosopher(int i) {
        return new Philosopher(i+1, forks[i], forks[(i+1)%numberOfPhilosophers], mealsLeftCounter[i], lock, condition);
    }
}
