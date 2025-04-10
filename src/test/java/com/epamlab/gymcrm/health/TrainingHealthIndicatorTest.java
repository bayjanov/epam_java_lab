package com.epamlab.gymcrm.health;


import com.epamlab.gymcrm.training.health.TrainingHealthIndicator;
import com.epamlab.gymcrm.training.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TrainingHealthIndicatorTest {

    @Test
    void shouldReturnHealthUpWhenTrainingServiceWorks() {
        TrainingService mockService = mock(TrainingService.class);
        when(mockService.listAllTrainings()).thenReturn(Collections.emptyList());

        TrainingHealthIndicator indicator = new TrainingHealthIndicator(mockService);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("UP");
    }

    @Test
    void shouldReturnHealthDownOnException() {
        TrainingService mockService = mock(TrainingService.class);
        when(mockService.listAllTrainings()).thenThrow(new RuntimeException("DB error"));

        TrainingHealthIndicator indicator = new TrainingHealthIndicator(mockService);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
    }
}
