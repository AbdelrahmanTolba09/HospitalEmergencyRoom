
import java.util.Scanner;

public class Hospital {

    private WaitingRoom waitingRoom;
    private Semaphore treatmentRooms;

    private Doctor[] doctors;
    private Patient[] patients;

    public void start() {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Waiting room capacity: ");
        int capacity = scanner.nextInt();

        System.out.print("Number of doctors: ");
        int doctorCount = scanner.nextInt();

        System.out.print("Number of patients: ");
        int patientCount = scanner.nextInt();

        if (capacity <= 0 ||
                doctorCount <= 0 ||
                patientCount <= 0) {

            System.out.println(
                    "All inputs must be positive numbers."
            );

            scanner.close();
            return;
        }

        // Create shared resources
        waitingRoom = new WaitingRoom(capacity);

        // Exactly two treatment rooms
        treatmentRooms = new Semaphore(2);

        // Create doctors and patients
        doctors = new Doctor[doctorCount];
        patients = new Patient[patientCount];

        // Start doctors first
        for (int i = 0; i < doctorCount; i++) {

            doctors[i] = new Doctor(
                    i + 1,
                    waitingRoom,
                    treatmentRooms
            );

            doctors[i].start();
        }

        // Start patients
        for (int i = 0; i < patientCount; i++) {

            patients[i] = new Patient(
                    i + 1,
                    waitingRoom
            );

            patients[i].start();
        }

        try {

            // Wait until every patient finishes
            for (Patient patient : patients) {
                patient.join();
            }

            // Send one stop signal to each doctor
            for (int i = 0; i < doctorCount; i++) {

                waitingRoom.put(
                        Patient.createPoison()
                );
            }

            // Wait until all doctors stop
            for (Doctor doctor : doctors) {
                doctor.join();
            }

            System.out.println();
            System.out.println(
                    "All patients have been treated."
            );

            System.out.println(
                    "Simulation completed."
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "Simulation was interrupted."
            );
        }

        scanner.close();
    }
}
