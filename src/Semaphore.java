
public class Semaphore {

    private int permits;

    public Semaphore(int permits) {
        this.permits = permits;
    }

    // Acquire a resource
    public synchronized void acquire()
            throws InterruptedException {

        while (permits == 0) {
            wait();
        }

        permits--;
    }

    // Try to acquire within a time limit
    public synchronized boolean tryAcquire(long timeoutMillis)
            throws InterruptedException {

        long deadline = System.nanoTime()
                + timeoutMillis * 1_000_000L;

        while (permits == 0) {

            long remaining = deadline - System.nanoTime();

            if (remaining <= 0) {
                return false;
            }

            long millis = remaining / 1_000_000L;
            int nanos = (int) (remaining % 1_000_000L);

            wait(millis, nanos);
        }

        permits--;
        return true;
    }

    // Release a resource
    public synchronized void release() {

        permits++;

        notifyAll();
    }
}
