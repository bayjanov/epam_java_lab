package com.epamlab.gymcrm.health;

import com.epamlab.gymcrm.trainee.health.TraineeHealthIndicator;
import com.epamlab.gymcrm.trainee.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TraineeHealthIndicatorTest {

    @Test
    void shouldReturnHealthUpWhenTraineeServiceWorks() {
        TraineeService mockService = mock(TraineeService.class);
        when(mockService.listAllTrainees()).thenReturn(Collections.emptyList());

        TraineeHealthIndicator indicator = new TraineeHealthIndicator(mockService);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("UP");
    }

    @Test
    void shouldReturnHealthDownOnException() {
        TraineeService mockService = mock(TraineeService.class);
        when(mockService.listAllTrainees()).thenThrow(new RuntimeException("DB error"));

        TraineeHealthIndicator indicator = new TraineeHealthIndicator(mockService);
        Health health = indicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
    }
}
