package com.epamlab.gymcrm.trainer.dto;

import com.epamlab.gymcrm.trainer.model.Trainer;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TrainerRegistrationResponse {
    private final Trainer trainer;
    private final String rawPassword;

    public TrainerRegistrationResponse(Trainer trainer, String rawPassword) {
        this.trainer = trainer;
        this.rawPassword = rawPassword;
    }
}
