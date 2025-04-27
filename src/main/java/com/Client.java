package com;

public class Client {
    private int clientId;
    private String fullName;
    private String residentialAddress;
    private String contactNumber;

    public Client(int clientId, String fullName, String residentialAddress, String contactNumber) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.residentialAddress = residentialAddress;
        this.contactNumber = contactNumber;
    }

    public int getClientId() {
        return clientId;
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
}
