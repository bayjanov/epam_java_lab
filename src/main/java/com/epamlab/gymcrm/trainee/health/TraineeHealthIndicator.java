package com.epamlab.gymcrm.trainee.health;

import com.epamlab.gymcrm.trainee.service.TraineeService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TraineeHealthIndicator implements HealthIndicator {

    private final TraineeService traineeService;

    public TraineeHealthIndicator(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Override
    public Health health() {
        try {
            int count = traineeService.listAllTrainees().size();
            return Health.up()
                    .withDetail("traineeService", "UP")
                    .withDetail("traineeCount", count)
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("traineeService", "DOWN")
                    .build();
        }
    }
}
