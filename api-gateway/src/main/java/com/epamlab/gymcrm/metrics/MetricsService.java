package com.epamlab.gymcrm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsService {

    private final Counter trainerLoginCounter;
    private final Counter traineeLoginCounter;
    private final Counter trainingAddedCounter;

    public MetricsService(MeterRegistry meterRegistry) {
        this.trainerLoginCounter = Counter.builder("gymcrm.trainer.login.count")
                .description("Count of successful trainer logins")
                .register(meterRegistry);

        this.traineeLoginCounter = Counter.builder("gymcrm.trainee.login.count")
                .description("Count of successful trainee logins")
                .register(meterRegistry);

        this.trainingAddedCounter = Counter.builder("gymcrm.training.added.count")
                .description("Count of training sessions added")
                .register(meterRegistry);
    }

    public void incrementTrainerLogin() {
        trainerLoginCounter.increment();
    }

    public void incrementTraineeLogin() {
        traineeLoginCounter.increment();
    }

    public void incrementTrainingAdded() {
        trainingAddedCounter.increment();
    }
}
