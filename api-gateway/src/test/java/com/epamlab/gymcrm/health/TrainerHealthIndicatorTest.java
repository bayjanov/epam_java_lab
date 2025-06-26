package com.epamlab.gymcrm.health;


import com.epamlab.gymcrm.trainer.health.TrainerHealthIndicator;
import com.epamlab.gymcrm.trainer.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TrainerHealthIndicatorTest {

    @Test
    void shouldReturnHealthUpWhenTrainerServiceWorks() {
        TrainerService mockService = mock(TrainerService.class);
        when(mockService.listAllTrainers()).thenReturn(Collections.emptyList());

        TrainerHealthIndicator indicator = new TrainerHealthIndicator(mockService);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("UP");
    }

    @Test
    void shouldReturnHealthDownOnException() {
        TrainerService mockService = mock(TrainerService.class);
        when(mockService.listAllTrainers()).thenThrow(new RuntimeException("DB error"));

        TrainerHealthIndicator indicator = new TrainerHealthIndicator(mockService);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
    }
}
