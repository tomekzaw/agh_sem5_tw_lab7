package waiter;

import base.IFork;
import base.AbstractPhilosopher;

import java.util.concurrent.atomic.AtomicInteger;

public class Philosopher extends AbstractPhilosopher {
        private final IFork left, right;
        private final Waiter waiter;

    Philosopher(int id, IFork left, IFork right, Waiter waiter, AtomicInteger meals) {
        super(id, meals);
        this.left = left;
        this.right = right;
        this.waiter = waiter;
    }

    protected void eat() throws InterruptedException {
        startMeasurement();
        waiter.acquire();
        try {
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
        } finally {
            waiter.release();
        }
    }
}
