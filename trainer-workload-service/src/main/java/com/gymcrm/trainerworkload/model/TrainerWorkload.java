package com.gymcrm.trainerworkload.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class TrainerWorkload {
    @Id
    private String username;

    private String firstName;
    private String lastName;
    private boolean isActive;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<WorkloadYear> years =  new ArrayList<>();

    public TrainerWorkload(String username, String firstName, String lastName, boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    // Required no-arg constructor for JPA
    public TrainerWorkload() {}
}
