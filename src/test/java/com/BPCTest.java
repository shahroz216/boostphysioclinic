package com;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class BPCTest {
    private ClinicManager clinicManager;

    @Before
    public void setUp() {
        clinicManager = new ClinicManager();
    }

    @Test
    public void testRegisterClient() {
        int initialClientCount = clinicManager.getClients().size();
        clinicManager.registerClient("John Doe", "123 Elm Street", "1234567890");
        assertEquals(initialClientCount + 1, clinicManager.getClients().size());
        Client newClient = clinicManager.getClients().get(clinicManager.getClients().size() - 1);
        assertEquals("John Doe", newClient.getFullName());
        assertEquals("123 Elm Street", newClient.getResidentialAddress());
        assertEquals("1234567890", newClient.getContactNumber());
    }

    @Test
    public void testRemoveClient() {
        Client client = clinicManager.getClients().get(0);
        int clientId = client.getClientId();
        int initialClientCount = clinicManager.getClients().size();

        Schedule schedule = clinicManager.getTherapistsBySpecialization("PhysioTherapy").get(0).getAppointments().get(0);
        clinicManager.reserveSchedule(clientId, schedule.getScheduleId());

        clinicManager.removeClient(clientId);
        assertEquals(initialClientCount - 1, clinicManager.getClients().size());
        assertNull(clinicManager.findClientById(clientId));
        assertTrue(schedule.isFree());
    }

    @Test
    public void testReserveSchedule() {
        Client client = clinicManager.getClients().get(0);
        int clientId = client.getClientId();

        Schedule schedule = clinicManager.getTherapistsBySpecialization("PhysioTherapy").get(0).getAppointments().get(0);
        int scheduleId = schedule.getScheduleId();

        boolean result = clinicManager.reserveSchedule(clientId, scheduleId);
        assertTrue(result);
        assertFalse(schedule.isFree());
        assertEquals("booked", schedule.getRegistration().getSessionStatus());
        assertEquals(client, schedule.getRegistration().getClient());
    }

    @Test
    public void testModifyRegistration() {
        Client client = clinicManager.getClients().get(0);
        int clientId = client.getClientId();

        Schedule schedule1 = clinicManager.getTherapistsBySpecialization("PhysioTherapy").get(0).getAppointments().get(0);
        int scheduleId1 = schedule1.getScheduleId();
        clinicManager.reserveSchedule(clientId, scheduleId1);

        Registration registration = schedule1.getRegistration();
        int registrationId = registration.getRegistrationId();

        Schedule schedule2 = clinicManager.getTherapistsBySpecialization("PhysioTherapy").get(0).getAppointments().get(1);
        int scheduleId2 = schedule2.getScheduleId();

        boolean result = clinicManager.modifyRegistration(registrationId, scheduleId2);
        assertTrue(result);
        assertTrue(schedule1.isFree());
        assertFalse(schedule2.isFree());
        assertEquals(schedule2, registration.getSchedule());
    }

    @Test
    public void testGetTherapistsBySpecialization() {
        List<Therapist> therapists = clinicManager.getTherapistsBySpecialization("Osteopathy");
        assertEquals(1, therapists.size());
        assertEquals("Dr. Emily Davis", therapists.get(0).getFullName());

        therapists = clinicManager.getTherapistsBySpecialization("PhysioTherapy");
        assertEquals(2, therapists.size());
        assertTrue(therapists.stream().anyMatch(t -> t.getFullName().equals("Dr. Sarah Wilson")));
        assertTrue(therapists.stream().anyMatch(t -> t.getFullName().equals("Dr. Michael Brown")));
    }
}