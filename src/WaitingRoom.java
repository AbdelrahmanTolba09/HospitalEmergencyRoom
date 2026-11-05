
import java.util.LinkedList;
import java.util.Queue;

public class WaitingRoom {

    private Queue<Patient> patients;

    private Semaphore empty;
    private Semaphore full;
    private Semaphore mutex;

    public WaitingRoom(int capacity) {

        patients = new LinkedList<>();

        empty = new Semaphore(capacity);
        full = new Semaphore(0);
        mutex = new Semaphore(1);
    }

    // Producer: Patient enters the waiting room
    public void put(Patient patient)
            throws InterruptedException {

        boolean gotSeat = empty.tryAcquire(0);

        if (!gotSeat) {

            if (!patient.isPoison()) {
                System.out.println(
                        "The waiting room is full."
                );

                System.out.println(
                        patient.getPatientName()
                                + " is waiting outside for an available seat."
                );
            }

            empty.acquire();
        }

        mutex.acquire();

        try {

            patients.add(patient);

            if (!patient.isPoison()) {
                System.out.println(
                        patient.getPatientName()
                                + " entered the waiting room."
                );
            }

        } finally {
            mutex.release();
        }

        full.release();
    }

    // Consumer: Doctor takes a patient
    public Patient take() throws InterruptedException {

        full.acquire();

        mutex.acquire();

        Patient patient;

        try {
            patient = patients.remove();

        } finally {
            mutex.release();
        }

        empty.release();

        return patient;
    }
}
