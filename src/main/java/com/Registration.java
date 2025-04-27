package com;

public class Registration {
    private int registrationId;
    private Client client;
    private Schedule schedule;
    private String sessionStatus;

    public Registration(int registrationId, Client client, Schedule schedule) {
        this.registrationId = registrationId;
        this.client = client;
        this.schedule = schedule;
        this.sessionStatus = "booked";
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public Client getClient() {
        return client;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
