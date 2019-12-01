package base;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

abstract public class AbstractPhilosopher extends Thread {
    private final int id;
    private final AtomicInteger mealsLeftCounter;
    private final AtomicInteger mealsEatenCounter = new AtomicInteger();

    private final long eatingTime = 10; // milliseconds
    private final long sleepingTime = 10; // milliseconds

    private long measurementStartTime;
    private List<Long> measurements = new ArrayList<>();

    public AbstractPhilosopher(final int id, AtomicInteger mealsLeftCounter) {
        this.id = id;
        this.mealsLeftCounter = mealsLeftCounter;
    }

    abstract protected void eat() throws InterruptedException;

    protected void eating() throws InterruptedException {
        // System.out.println("[" + System.nanoTime() + "] Philosopher #" + id + " is eating... " + mealsLeftCounter.get() + " meals left");
        System.out.println(id + " " + mealsLeftCounter.get());
        Thread.sleep((long) (Math.random() * eatingTime));
    }

    protected void sleep() throws InterruptedException {
        // System.out.println("[" + System.nanoTime() + "] Philosopher #" + id + " is sleeping...");
        Thread.sleep((long) (Math.random() * sleepingTime));
    }

    protected void startMeasurement() {
        measurementStartTime = System.nanoTime();
    }

    protected void endMeasurement() {
        long measurementEndTime = System.nanoTime();
        measurements.add(measurementEndTime - measurementStartTime);
    }

    public void printMeasurements(PrintStream out) {
        out.println(measurements.stream().map(Object::toString).collect(Collectors.joining(";")));
    }

    @Override
    public void run() {
        try {
            while (mealsLeftCounter.getAndDecrement() > 0) {
                mealsEatenCounter.incrementAndGet();
                eat();
                sleep();
            }
        } catch (InterruptedException __) {}
    }
}
