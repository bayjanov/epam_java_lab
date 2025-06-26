package com.epamlab.gymcrm.training.health;

import com.epamlab.gymcrm.training.service.TrainingService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingHealthIndicator implements HealthIndicator {

    private final TrainingService trainingService;

    public TrainingHealthIndicator(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Override
    public Health health() {
        try {
            int count = trainingService.listAllTrainings().size();
            return Health.up()
                    .withDetail("trainingService", "UP")
                    .withDetail("trainingCount", count)
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("trainingService", "DOWN")
                    .build();
        }
    }
}
