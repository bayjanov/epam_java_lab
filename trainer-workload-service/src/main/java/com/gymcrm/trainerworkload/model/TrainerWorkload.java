package com.gymcrm.trainerworkload.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "trainer_workloads")
public class TrainerWorkload {
    @Id
    private String username;   // Username as ID

    @Indexed
    private String firstName;

    @Indexed
    private String lastName;

    private boolean isActive;

    private List<WorkloadYear> years = new ArrayList<>();

    public TrainerWorkload(String username, String firstName, String lastName, boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    public TrainerWorkload() {}
}
