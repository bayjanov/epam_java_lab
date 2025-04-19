package com.epamlab.gymcrm.trainer.dto;

import com.epamlab.gymcrm.trainer.model.Trainer;

public class TrainerRegistrationResponse {
    private final Trainer trainer;
    private final String rawPassword;

    public TrainerRegistrationResponse(Trainer trainer, String rawPassword) {
        this.trainer = trainer;
        this.rawPassword = rawPassword;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public String getRawPassword() {
        return rawPassword;
    }
}
