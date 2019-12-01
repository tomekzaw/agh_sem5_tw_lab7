package simultaneous;

import base.IFork;
import base.AbstractPhilosopher;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class Philosopher extends AbstractPhilosopher {
    private final IFork left, right;
    private final Lock lock;
    private final Condition condition;

    Philosopher(int id, IFork left, IFork right, AtomicInteger meals, Lock lock, Condition condition) {
        super(id, meals);
        this.left = left;
        this.right = right;
        this.lock = lock;
        this.condition = condition;
    }

    protected void eat() throws InterruptedException {
        startMeasurement();
        lock.lock();
        try {
            while (left.isTaken() || right.isTaken()) {
                condition.await();
            }
            endMeasurement();

            left.acquire();
            right.acquire();

            eating();

            left.release();
            right.release();

            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
