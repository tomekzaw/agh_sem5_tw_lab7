package naive;

import base.IFork;
import base.AbstractPhilosopher;

import java.util.concurrent.atomic.AtomicInteger;

public class Philosopher extends AbstractPhilosopher {
    private final IFork left, right;

    Philosopher(int id, IFork left, IFork right, AtomicInteger meals) {
        super(id, meals);
        this.left = left;
        this.right = right;
    }

    protected void eat() throws InterruptedException {
        startMeasurement();
        left.acquire();
        try {
            right.acquire();
            try {
                endMeasurement();
                eating();
            } finally {
                right.release();
            }
        } finally {
            left.release();
        }
    }
}
