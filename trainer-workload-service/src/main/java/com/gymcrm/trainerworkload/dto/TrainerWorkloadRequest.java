package com.gymcrm.trainerworkload.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainerWorkloadRequest {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDate trainingDate;
    private int duration; // mins

    public enum ActionType {
        ADD,
        DELETE
    }

    private ActionType actionType;
}
