package com.gymcrm.trainerworkload.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainerWorkloadRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private boolean isActive;

    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int duration;

    @NotNull(message = "Action type is required")
    private ActionType actionType;

    public enum ActionType {
        ADD,
        DELETE
    }
}
