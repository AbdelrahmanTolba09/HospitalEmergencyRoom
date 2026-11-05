
public class Patient extends Thread {

    private int id;
    private WaitingRoom waitingRoom;

    private boolean treated = false;
    private boolean retry = false;
    private boolean poison = false;

    public Patient(int id, WaitingRoom waitingRoom) {

        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    private Patient() {
        poison = true;
    }

    public static Patient createPoison() {
        return new Patient();
    }

    public boolean isPoison() {
        return poison;
    }

    public String getPatientName() {
        return "P" + id;
    }


    public synchronized void retryTreatment() {

        retry = true;
        notifyAll();
    }

    public synchronized void finishTreatment() {

        treated = true;
        notifyAll();
    }

    private synchronized boolean waitForResult()
            throws InterruptedException {

        while (!treated && !retry) {
            wait();
        }

        if (treated) {
            return true;
        }

        retry = false;
        return false;
    }

    @Override
    public void run() {

        try {

            System.out.println(
                    getPatientName() + " arrived."
            );

            while (true) {

                waitingRoom.put(this);

                boolean completed = waitForResult();

                if (completed) {
                    break;
                }
            }

            System.out.println(
                    getPatientName() + " left."
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        }
    }
}
