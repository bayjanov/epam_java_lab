package com.epamlab.gymcrm.trainee.dto;

import com.epamlab.gymcrm.trainee.model.Trainee;

public class TraineeRegistrationResponse {
    private Trainee trainee;
    private String generatedPassword;

    public TraineeRegistrationResponse() {
    }

    public TraineeRegistrationResponse(Trainee trainee, String generatedPassword) {
        this.trainee = trainee;
        this.generatedPassword = generatedPassword;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    public void setGeneratedPassword(String generatedPassword) {
        this.generatedPassword = generatedPassword;
    }
}
