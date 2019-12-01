package base;

import java.util.concurrent.Semaphore;

public class SemaphoreFork extends Semaphore implements IFork {
    public SemaphoreFork() {
        super(1);
    }

    public boolean isTaken() {
        return this.availablePermits() == 0;
    }
}
