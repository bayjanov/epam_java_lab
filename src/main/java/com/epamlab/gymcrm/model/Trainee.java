package com.epamlab.gymcrm.model;

import java.time.LocalDate;

public class Trainee extends User {
    private static long idCounter = 1;

    private final Long userId;
    private LocalDate dateOfBirth;
    private String address;

    public Trainee() {
        this.userId = idCounter++;
    }

    public Trainee(String firstName, String lastName, boolean isActive,
                   LocalDate dateOfBirth, String address) {
        super(firstName, lastName, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userId = idCounter++;
    }

    public Long getUserId() { return userId; }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "userId=" + userId +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", active=" + isActive() +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}
