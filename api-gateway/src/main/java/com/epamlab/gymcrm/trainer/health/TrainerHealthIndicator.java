package com.epamlab.gymcrm.trainer.health;

import com.epamlab.gymcrm.trainer.service.TrainerService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainerHealthIndicator implements HealthIndicator {

    private final TrainerService trainerService;

    public TrainerHealthIndicator(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Override
    public Health health() {
        try {
            int count = trainerService.listAllTrainers().size();
            return Health.up()
                    .withDetail("trainerService", "UP")
                    .withDetail("trainerCount", count)
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("trainerService", "DOWN")
                    .build();
        }
    }
}
