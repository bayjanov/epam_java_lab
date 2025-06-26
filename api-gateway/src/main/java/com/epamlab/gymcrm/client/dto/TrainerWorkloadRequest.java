package com.epamlab.gymcrm.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadRequest {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private LocalDate trainingDate;
    private int duration;

    public enum ActionType { ADD, DELETE }
    private ActionType actionType;
}
