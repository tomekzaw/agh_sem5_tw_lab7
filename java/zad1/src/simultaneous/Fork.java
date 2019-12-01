package simultaneous;

import base.IFork;

class Fork implements IFork {
    private boolean taken = false;

    public void acquire() throws InterruptedException {
        this.taken = true;
    }

    public void release() throws InterruptedException {
        this.taken = false;
    }

    public boolean isTaken() {
        return this.taken;
    }
}
