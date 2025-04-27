package com;

import java.time.LocalDateTime;

public class Schedule {
    private int scheduleId;
    private Therapist therapist;
    private ServiceType serviceType;
    private LocalDateTime dateTime;
    private Registration registration;

    public Schedule(int scheduleId, Therapist therapist, ServiceType serviceType, LocalDateTime dateTime) {
        this.scheduleId = scheduleId;
        this.therapist = therapist;
        this.serviceType = serviceType;
        this.dateTime = dateTime;
        this.registration = null;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public Therapist getTherapist() {
        return therapist;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public boolean isFree() {
        return registration == null;
    }
}
