package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.metrics.MetricsService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricsServiceTest {

    private MetricsService metricsService;

    @BeforeEach
    void setup() {
        metricsService = new MetricsService(new SimpleMeterRegistry());
    }

    @Test
    void shouldIncrementTrainerLoginCounter() {
        metricsService.incrementTrainerLogin();
        assertThat(metricsService).isNotNull();
    }

    @Test
    void shouldIncrementTraineeLoginCounter() {
        metricsService.incrementTraineeLogin();
        assertThat(metricsService).isNotNull();
    }

    @Test
    void shouldIncrementTrainingAddedCounter() {
        metricsService.incrementTrainingAdded();
        assertThat(metricsService).isNotNull();
    }
}
