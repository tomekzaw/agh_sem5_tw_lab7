package asymmetric;

import base.IFork;
import base.AbstractPhilosopher;

import java.util.concurrent.atomic.AtomicInteger;

public class Philosopher extends AbstractPhilosopher {
    private final IFork first, second;

    Philosopher(int id, IFork left, IFork right, AtomicInteger meals) {
        super(id, meals);
        if (id % 2 == 1) {
            this.first = left;
            this.second = right;
        } else {
            this.first = right;
            this.second = left;
        }
    }

    protected void eat() throws InterruptedException {
        startMeasurement();
        first.acquire();
        try {
            second.acquire();
            try {
                endMeasurement();
                eating();
            } finally {
                second.release();
            }
        } finally {
            first.release();
        }
    }
}