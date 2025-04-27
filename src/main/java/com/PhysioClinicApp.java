package com;

import java.util.List;
import java.util.Scanner;

public class PhysioClinicApp {
    public static void main(String[] args) {
        ClinicManager manager = new ClinicManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMainMenu();
            int choice = getIntInput(scanner, "Enter your choice: ");

            switch (choice) {
                case 1: // Register Client
                    System.out.print("Enter full name: ");
                    String fullName = scanner.nextLine();
                    System.out.print("Enter residential address: ");
                    String residentialAddress = scanner.nextLine();
                    System.out.print("Enter contact number: ");
                    String contactNumber = scanner.nextLine();
                    manager.registerClient(fullName, residentialAddress, contactNumber);
                    break;

                case 2: // Remove Client
                    displayClients(manager);
                    int clientId = getIntInput(scanner, "Enter client ID to remove: ");
                    manager.removeClient(clientId);
                    break;

                case 3: // Reserve Schedule
                    displayClients(manager);
                    clientId = getIntInput(scanner, "Enter client ID: ");
                    Client client = manager.findClientById(clientId);

                    if (client == null) {
                        System.out.println("Client not found.");
                        break;
                    }

                    System.out.println("1. Search by specialization\n2. Search by therapist name");
                    int searchType = getIntInput(scanner, "Select search method: ");

                    if (searchType == 1) {
                        System.out.print("Enter specialization (PhysioTherapy, Rehabilitation, Osteopathy): ");
                        String specialization = scanner.nextLine();
                        List<Therapist> matchedTherapists = manager.getTherapistsBySpecialization(specialization);

                        if (matchedTherapists.isEmpty()) {
                            System.out.println("No therapists found with that specialization.");
                            break;
                        }

                        processReservation(scanner, manager, clientId, matchedTherapists);
                    } else if (searchType == 2) {
                        System.out.print("Enter therapist name: ");
                        String therapistName = scanner.nextLine().trim();
                        Therapist therapist = manager.findTherapistByName(therapistName);

                        if (therapist == null) {
                            System.out.println("Therapist not found.");
                            break;
                        }

                        processReservation(scanner, manager, clientId, List.of(therapist));
                    }
                    break;

                case 4: // Modify Registration
                    displayRegistrations(manager);
                    int registrationId = getIntInput(scanner, "Enter registration ID to modify: ");
                    System.out.println("Select new schedule:");

                    searchType = getIntInput(scanner, "1. Search by specialization\n2. Search by therapist name: ");

                    if (searchType == 1) {
                        System.out.print("Enter specialization: ");
                        String specialization = scanner.nextLine();
                        List<Therapist> matchedTherapists = manager.getTherapistsBySpecialization(specialization);

                        if (matchedTherapists.isEmpty()) {
                            System.out.println("No therapists found with that specialization.");
                            break;
                        }

                        processModification(scanner, manager, registrationId, matchedTherapists);
                    } else if (searchType == 2) {
                        System.out.print("Enter therapist name: ");
                        String therapistName = scanner.nextLine();
                        Therapist therapist = manager.findTherapistByName(therapistName);

                        if (therapist == null) {
                            System.out.println("Therapist not found.");
                            break;
                        }

                        processModification(scanner, manager, registrationId, List.of(therapist));
                    }
                    break;

                case 5: // Cancel Registration
                    displayRegistrations(manager);
                    registrationId = getIntInput(scanner, "Enter registration ID to cancel: ");

                    if (manager.cancelRegistration(registrationId)) {
                        System.out.println("Registration cancelled successfully.");
                    } else {
                        System.out.println("Failed to cancel registration.");
                    }
                    break;

                case 6: // Complete Registration
                    displayRegistrations(manager);
                    registrationId = getIntInput(scanner, "Enter registration ID to mark as completed: ");

                    if (manager.completeRegistration(registrationId)) {
                        System.out.println("Registration marked as completed.");
                    } else {
                        System.out.println("Failed to mark registration as completed.");
                    }
                    break;

                case 7: // Generate Report
                    manager.generateActivityReport();
                    break;

                case 8: // Exit
                    System.out.println("******** Thank You for Using PhysioClinic Management System ***********");
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== PhysioClinic Management System ===");
        System.out.println("1. Register client");
        System.out.println("2. Remove client");
        System.out.println("3. Reserve schedule");
        System.out.println("4. Modify registration");
        System.out.println("5. Cancel registration");
        System.out.println("6. Complete registration");
        System.out.println("7. Generate activity report");
        System.out.println("8. Exit");
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static void displayClients(ClinicManager manager) {
        System.out.println("\nClients:");
        manager.getClients().forEach(c -> System.out.println(c.getClientId() + ": " + c.getFullName()));
    }

    private static void displayRegistrations(ClinicManager manager) {
        System.out.println("\nRegistrations:");
        manager.getRegistrations().forEach(r -> System.out.printf(
                "ID: %d, Client: %s, Service: %s, Time: %s, Status: %s%n",
                r.getRegistrationId(),
                r.getClient().getFullName(),
                r.getSchedule().getServiceType().getTitle(),
                r.getSchedule().getDateTime(),
                r.getSessionStatus()));
    }

    private static void processReservation(Scanner scanner, ClinicManager manager, int clientId, List<Therapist> therapists) {
        for (Therapist t : therapists) {
            List<Schedule> availableSlots = manager.getAvailableSlots(t);

            if (!availableSlots.isEmpty()) {
                System.out.println("\n" + t.getFullName() + "'s Available Slots:");
                availableSlots.forEach(s -> System.out.printf(
                        "ID: %d, Service: %s, Time: %s%n",
                        s.getScheduleId(),
                        s.getServiceType().getTitle(),
                        s.getDateTime()));
            }
        }

        int scheduleId = getIntInput(scanner, "Enter schedule ID to reserve: ");

        if (manager.reserveSchedule(clientId, scheduleId)) {
            System.out.println("Reservation successful.");
        } else {
            System.out.println("Reservation failed (slot unavailable or time conflict).");
        }
    }

    private static void processModification(Scanner scanner, ClinicManager manager, int registrationId, List<Therapist> therapists) {
        for (Therapist t : therapists) {
            List<Schedule> availableSlots = manager.getAvailableSlots(t);

            if (!availableSlots.isEmpty()) {
                System.out.println("\n" + t.getFullName() + "'s Available Slots:");
                availableSlots.forEach(s -> System.out.printf(
                        "ID: %d, Service: %s, Time: %s%n",
                        s.getScheduleId(),
                        s.getServiceType().getTitle(),
                        s.getDateTime()));
            }
        }

        int newScheduleId = getIntInput(scanner, "Enter new schedule ID: ");

        if (manager.modifyRegistration(registrationId, newScheduleId)) {
            System.out.println("Registration modified successfully.");
        } else {
            System.out.println("Modification failed (slot unavailable or time conflict).");
        }
    }
}
