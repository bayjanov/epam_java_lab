package com.epamlab.gymcrm.model;

public class User {
    String firstName;
    String lastName;
    String username;
    String password;
    boolean isActive;

    protected User(){}

    protected User(String firstName, String lastName, boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }


}

