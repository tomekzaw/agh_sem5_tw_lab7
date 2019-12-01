package base;

public interface IFork {
    void acquire() throws InterruptedException;
    void release() throws InterruptedException;
    boolean isTaken();
}
