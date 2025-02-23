package com.epamlab.gymcrm.model;

public class Trainer extends User {
    private static long idCounter = 1;

    private final Long userId;
    private String specialization;

    public Trainer() {
        this.userId = idCounter++;
    }

    public Trainer(String firstname, String lastName, String specialization, boolean isActive) {
        super(firstname, lastName, isActive);
        this.specialization = specialization;
        this.userId = idCounter++;
    }

    public Long getUserId() { return userId; }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "userId=" + userId +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", active=" + isActive() +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
