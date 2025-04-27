package com;

public class ServiceType {
    private String title;
    private Specialization specialization;

    public ServiceType(String title, Specialization specialization) {
        this.title = title;
        this.specialization = specialization;
    }

    public String getTitle() {
        return title;
    }

    public Specialization getSpecialization() {
        return specialization;
    }
}
