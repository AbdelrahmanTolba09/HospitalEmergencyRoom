
import java.util.Random;

public class Doctor extends Thread {

    private int id;
    private WaitingRoom waitingRoom;
    private Semaphore treatmentRooms;

    private Random random = new Random();

    public Doctor(
            int id,
            WaitingRoom waitingRoom,
            Semaphore treatmentRooms) {

        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
    }

    private String getDoctorName() {
        return "Doctor " + id;
    }

    @Override
    public void run() {

        try {

            while (true) {

                Patient patient = waitingRoom.take();


                if (patient.isPoison()) {
                    break;
                }

                System.out.println(
                        getDoctorName() + " is assigned to "
                                + patient.getPatientName() + "."
                );

                System.out.println(
                        getDoctorName()
                                + " is trying to acquire a treatment room."
                );

                boolean acquired =
                        treatmentRooms.tryAcquire(3000);

                if (!acquired) {

                    System.out.println(
                            getDoctorName()
                                    + " could not acquire a treatment room."
                    );

                    System.out.println(
                            getDoctorName()
                                    + " is sending "
                                    + patient.getPatientName()
                                    + " back to the waiting room."
                    );

                    patient.retryTreatment();

                    System.out.println(
                            getDoctorName() + " is now available."
                    );

                    continue;
                }

                try {

                    System.out.println(
                            getDoctorName()
                                    + " acquired a treatment room."
                    );

                    System.out.println(
                            getDoctorName()
                                    + " begins treating "
                                    + patient.getPatientName() + "."
                    );


                    int treatmentTime =
                            1000 + random.nextInt(2001);

                    Thread.sleep(treatmentTime);

                    System.out.println(
                            getDoctorName()
                                    + " finished treating "
                                    + patient.getPatientName() + "."
                    );

                } finally {

                    treatmentRooms.release();

                    System.out.println(
                            getDoctorName()
                                    + " released the treatment room."
                    );
                }

                patient.finishTreatment();

                System.out.println(
                        getDoctorName() + " is now available."
                );
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}
