package com.epamlab.gymcrm.trainer.model;

import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
public class Trainer extends User {

    @Column
    private String specialization;

    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees = new HashSet<>();

    public Trainer() {
        super();
    }

    public Trainer(String firstname, String lastName, String specialization, boolean isActive) {
        super(firstname, lastName, isActive);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Set<Trainee> getTrainees() {
        return trainees;
    }
    public void setTrainees(Set<Trainee> trainees) {
        this.trainees = trainees;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "userId=" + this.getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", active=" + isActive() +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
