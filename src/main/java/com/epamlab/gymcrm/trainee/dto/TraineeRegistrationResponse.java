package com.epamlab.gymcrm.trainee.dto;

import com.epamlab.gymcrm.trainee.model.Trainee;

public class TraineeRegistrationResponse {
    private final Trainee trainee;
    private final String rawPassword;

    public TraineeRegistrationResponse(Trainee trainee, String rawPassword) {
        this.trainee = trainee;
        this.rawPassword = rawPassword;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public String getRawPassword() {
        return rawPassword;
    }
}
