package com;

import java.util.ArrayList;
import java.util.List;

public class Therapist {
    private int therapistId;
    private String fullName;
    private String residentialAddress;
    private String contactNumber;
    private List<Specialization> specializations;
    private List<Schedule> appointments;

    public Therapist(int therapistId, String fullName, String residentialAddress, String contactNumber, List<Specialization> specializations) {
        this.therapistId = therapistId;
        this.fullName = fullName;
        this.residentialAddress = residentialAddress;
        this.contactNumber = contactNumber;
        this.specializations = new ArrayList<>(specializations);
        this.appointments = new ArrayList<>();
    }

    public int getTherapistId() {
        return therapistId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public List<Schedule> getAppointments() {
        return appointments;
    }

    public void addSchedule(Schedule schedule) {
        appointments.add(schedule);
    }
}
