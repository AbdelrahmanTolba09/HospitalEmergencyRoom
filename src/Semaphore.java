
public class Semaphore {

    private int permits;

    public Semaphore(int permits) {
        this.permits = permits;
    }

    public synchronized void acquire()
            throws InterruptedException {

        while (permits == 0) {
            wait();
        }

        permits--;
    }

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

    public synchronized void release() {

        permits++;

        notifyAll();
    }
}
