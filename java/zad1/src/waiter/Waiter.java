package waiter;

import java.util.concurrent.Semaphore;

public class Waiter extends Semaphore {
    Waiter(int numberOfPhilosophers) {
        super(numberOfPhilosophers-1);
    }
}
