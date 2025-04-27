package com;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

public class ClinicManager {
    private List<Therapist> therapists;
    private List<Client> clients;
    private List<Registration> registrations;
    private int nextClientId;
    private int nextScheduleId;
    private int nextRegistrationId;

    public ClinicManager() {
        therapists = new ArrayList<>();
        clients = new ArrayList<>();
        registrations = new ArrayList<>();
        nextClientId = 1;
        nextScheduleId = 1;
        nextRegistrationId = 1;
        initializeSystemData();
    }

    private void initializeSystemData() {
        // Specializations
        Specialization physioTherapy = new Specialization("PhysioTherapy");
        Specialization rehabilitation = new Specialization("Rehabilitation");
        Specialization osteopathy = new Specialization("Osteopathy");

        // Service Types
        ServiceType neuralTherapy = new ServiceType("Neural therapy", physioTherapy);
        ServiceType acupuncture = new ServiceType("Acupuncture", physioTherapy);
        ServiceType therapeuticMassage = new ServiceType("Therapeutic massage", physioTherapy);
        ServiceType jointManipulation = new ServiceType("Joint manipulation", physioTherapy);
        ServiceType hydrotherapy = new ServiceType("Hydrotherapy", rehabilitation);

        // Therapists
        Therapist t1 = new Therapist(1, "Dr. Sarah Wilson", "45 Maple Avenue", "7894561230", List.of(physioTherapy, rehabilitation));
        Therapist t2 = new Therapist(2, "Dr. Michael Brown", "78 Oak Street", "4567891230", List.of(physioTherapy));
        Therapist t3 = new Therapist(3, "Dr. Emily Davis", "123 Pine Road", "1598746320", List.of(osteopathy));
        therapists.addAll(List.of(t1, t2, t3));

        // Schedule for May 2025 (4 weeks)
        LocalDateTime[][] slots = {
                // Week 1
                { LocalDateTime.of(2025, 5, 5, 9, 0), LocalDateTime.of(2025, 5, 6, 14, 0), LocalDateTime.of(2025, 5, 7, 11, 0) },
                // Week 2
                { LocalDateTime.of(2025, 5, 12, 10, 0), LocalDateTime.of(2025, 5, 13, 13, 0), LocalDateTime.of(2025, 5, 14, 15, 0) },
                // Week 3
                { LocalDateTime.of(2025, 5, 19, 11, 0), LocalDateTime.of(2025, 5, 20, 9, 0), LocalDateTime.of(2025, 5, 21, 16, 0) },
                // Week 4
                { LocalDateTime.of(2025, 5, 26, 13, 0), LocalDateTime.of(2025, 5, 27, 10, 0), LocalDateTime.of(2025, 5, 28, 14, 0) }
        };

        ServiceType[] t1Services = {therapeuticMassage, acupuncture, hydrotherapy};
        ServiceType[] t2Services = {neuralTherapy, jointManipulation, therapeuticMassage};
        ServiceType[] t3Services = {jointManipulation};

        // Create schedule entries
        for (int week = 0; week < 4; week++) {
            t1.addSchedule(new Schedule(nextScheduleId++, t1, t1Services[week % 3], slots[week][0]));
            t1.addSchedule(new Schedule(nextScheduleId++, t1, t1Services[(week + 1) % 3], slots[week][1]));
            t2.addSchedule(new Schedule(nextScheduleId++, t2, t2Services[week % 3], slots[week][0])); // Overlap with t1
            t2.addSchedule(new Schedule(nextScheduleId++, t2, t2Services[(week + 1) % 3], slots[week][2]));
            t3.addSchedule(new Schedule(nextScheduleId++, t3, t3Services[0], slots[week][1]));
        }

        // Generate 13 clients
        String[] clientNames = {
                "Emma Thompson", "James Wilson", "Olivia Martinez", "Noah Johnson",
                "Sophia Anderson", "William Taylor", "Isabella Thomas", "Lucas White",
                "Mia Harris", "Benjamin Martin", "Charlotte Lewis", "Alexander Walker", "Amelia King"
        };

        for (int i = 0; i < 13; i++) {
            clients.add(new Client(nextClientId++, clientNames[i], "Address " + (i + 1), "555999" + String.format("%04d", i)));
        }
    }

    // Register New Client
    public void registerClient(String fullName, String residentialAddress, String contactNumber) {
        clients.add(new Client(nextClientId++, fullName, residentialAddress, contactNumber));
        System.out.println("Client registered successfully");
    }

    // Remove Client
    public void removeClient(int clientId) {
        Client client = findClientById(clientId);
        if (client == null) {
            System.out.println("Client not found.");
            return;
        }

        registrations.removeIf(r -> {
            if (r.getClient().getClientId() == clientId) {
                r.getSchedule().setRegistration(null);
                return true;
            }
            return false;
        });

        clients.remove(client);
        System.out.println("Client removed and all reservations cancelled.");
    }

    // Reserve Schedule
    public boolean reserveSchedule(int clientId, int scheduleId) {
        Client client = findClientById(clientId);
        Schedule schedule = findScheduleById(scheduleId);

        if (client == null || schedule == null || !schedule.isFree()) {
            return false;
        }

        if (hasTimeConflict(client, schedule.getDateTime())) {
            return false;
        }

        Registration registration = new Registration(nextRegistrationId++, client, schedule);
        registrations.add(registration);
        schedule.setRegistration(registration);
        return true;
    }

    // Modify Registration
    public boolean modifyRegistration(int registrationId, int newScheduleId) {
        Registration registration = findRegistrationById(registrationId);
        Schedule newSchedule = findScheduleById(newScheduleId);

        if (registration == null || !registration.getSessionStatus().equals("booked") ||
                newSchedule == null || !newSchedule.isFree()) {
            return false;
        }

        Client client = registration.getClient();
        if (hasTimeConflict(client, newSchedule.getDateTime(), registrationId)) {
            return false;
        }

        Schedule oldSchedule = registration.getSchedule();
        oldSchedule.setRegistration(null);
        registration.setSchedule(newSchedule);
        newSchedule.setRegistration(registration);
        return true;
    }

    // Cancel Registration
    public boolean cancelRegistration(int registrationId) {
        Registration registration = findRegistrationById(registrationId);
        if (registration == null || !registration.getSessionStatus().equals("booked")) {
            return false;
        }

        registration.setSessionStatus("canceled");
        registration.getSchedule().setRegistration(null);
        return true;
    }

    // Complete Registration
    public boolean completeRegistration(int registrationId) {
        Registration registration = findRegistrationById(registrationId);
        if (registration == null || !registration.getSessionStatus().equals("booked")) {
            return false;
        }

        registration.setSessionStatus("attended");
        return true;
    }

    // Generate Activity Report
    public void generateActivityReport() {
        System.out.println("=== PhysioClinic Treatment Sessions Report ===");

        Map<Therapist, List<Schedule>> schedulesByTherapist = therapists.stream()
                .collect(Collectors.toMap(
                        t -> t,
                        Therapist::getAppointments,
                        (a, b) -> a,
                        HashMap::new
                ));

        for (Therapist t : therapists) {
            System.out.println("\nTherapist: " + t.getFullName());
            List<Schedule> therapistSchedules = schedulesByTherapist.getOrDefault(t, new ArrayList<>());

            List<Registration> therapistRegistrations = registrations.stream()
                    .filter(r -> r.getSchedule().getTherapist().equals(t))
                    .collect(Collectors.toList());

            for (Schedule s : therapistSchedules) {
                String clientName = "None";
                String status = "available";

                if (s.getRegistration() != null) {
                    clientName = s.getRegistration().getClient().getFullName();
                    status = s.getRegistration().getSessionStatus();
                } else {
                    Optional<Registration> cancelledRegistration = therapistRegistrations.stream()
                            .filter(r -> r.getSchedule().equals(s) && r.getSessionStatus().equals("canceled"))
                            .findFirst();

                    if (cancelledRegistration.isPresent()) {
                        clientName = cancelledRegistration.get().getClient().getFullName();
                        status = "canceled";
                    }
                }

                System.out.printf("Service: %s, Time: %s, Client: %s, Status: %s%n",
                        s.getServiceType().getTitle(), s.getDateTime(), clientName, status);
            }
        }

        System.out.println("\n=== Therapists Ranked by Completed Sessions ===");
        therapists.stream()
                .sorted(Comparator.comparingInt(this::countCompletedSessions).reversed())
                .forEach(t -> System.out.println(t.getFullName() + ": " + countCompletedSessions(t)));
    }

    // Helper Methods
    public Client findClientById(int id) {
        return clients.stream()
                .filter(c -> c.getClientId() == id)
                .findFirst()
                .orElse(null);
    }

    private Schedule findScheduleById(int id) {
        return therapists.stream()
                .flatMap(t -> t.getAppointments().stream())
                .filter(s -> s.getScheduleId() == id)
                .findFirst()
                .orElse(null);
    }

    private Registration findRegistrationById(int id) {
        return registrations.stream()
                .filter(r -> r.getRegistrationId() == id)
                .findFirst()
                .orElse(null);
    }

    private boolean hasTimeConflict(Client client, LocalDateTime time, int... excludeRegistrationId) {
        return registrations.stream()
                .anyMatch(r -> r.getClient().getClientId() == client.getClientId() &&
                        r.getSessionStatus().equals("booked") &&
                        r.getSchedule().getDateTime().equals(time) &&
                        (excludeRegistrationId.length == 0 || r.getRegistrationId() != excludeRegistrationId[0]));
    }

    private int countCompletedSessions(Therapist t) {
        return (int) registrations.stream()
                .filter(r -> r.getSchedule().getTherapist().equals(t) && r.getSessionStatus().equals("attended"))
                .count();
    }

    public List<Therapist> getTherapistsBySpecialization(String specializationTitle) {
        return therapists.stream()
                .filter(t -> t.getSpecializations().stream()
                        .anyMatch(s -> s.getTitle().equals(specializationTitle)))
                .toList();
    }

    public Therapist findTherapistByName(String name) {
        String normalizedName = name.replaceAll("'s\\b", "").trim().toLowerCase();
        return therapists.stream()
                .filter(t -> t.getFullName().toLowerCase().replace("dr. ", "").contains(normalizedName))
                .findFirst()
                .orElse(null);
    }

    public List<Schedule> getAvailableSlots(Therapist t) {
        return t.getAppointments().stream()
                .filter(Schedule::isFree)
                .toList();
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }
}
