package com.gymcrm.trainerworkload.service;

import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;
import com.gymcrm.trainerworkload.model.TrainerWorkload;
import com.gymcrm.trainerworkload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TrainerWorkloadServiceImplTest {

    private TrainerWorkloadRepository repository;
    private TrainerWorkloadServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(TrainerWorkloadRepository.class);
        service = new TrainerWorkloadServiceImpl(repository);
    }

    private TrainerWorkloadRequest buildRequest(String username, int year, int month, int duration, TrainerWorkloadRequest.ActionType actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername(username);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(LocalDate.of(year, month, 1));
        request.setDuration(duration);
        request.setActionType(actionType);
        return request;
    }

    @Test
    void shouldCreateNewTrainerWithWorkload() {
        // given
        TrainerWorkloadRequest request = buildRequest("trainer1", 2025, 7, 60, TrainerWorkloadRequest.ActionType.ADD);
        when(repository.findByUsername("trainer1")).thenReturn(Optional.empty());

        // when
        service.processWorkload(request, "tx-123");

        // then
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository).save(captor.capture());

        TrainerWorkload saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("trainer1");
        assertThat(saved.getYears()).hasSize(1);
        assertThat(saved.getYears().get(0).getMonths()).hasSize(1);
        assertThat(saved.getYears().get(0).getMonths().get(0).getTotalDuration()).isEqualTo(60);
    }

    @Test
    void shouldUpdateExistingTrainerWorkload() {
        // given
        TrainerWorkload existing = new TrainerWorkload("trainer1", "John", "Doe", true);
        existing.getYears().add(new com.gymcrm.trainerworkload.model.WorkloadYear(2025, new ArrayList<>()));
        existing.getYears().get(0).getMonths().add(new com.gymcrm.trainerworkload.model.WorkloadMonth(7, 60));

        when(repository.findByUsername("trainer1")).thenReturn(Optional.of(existing));

        TrainerWorkloadRequest request = buildRequest("trainer1", 2025, 7, 30, TrainerWorkloadRequest.ActionType.ADD);

        // when
        service.processWorkload(request, "tx-123");

        // then
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository).save(captor.capture());
        TrainerWorkload updated = captor.getValue();

        assertThat(updated.getYears().get(0).getMonths().get(0).getTotalDuration()).isEqualTo(90);
    }

    @Test
    void shouldCreateNewMonthIfNotExists() {
        // given
        TrainerWorkload existing = new TrainerWorkload("trainer1", "John", "Doe", true);
        existing.getYears().add(new com.gymcrm.trainerworkload.model.WorkloadYear(2025, new ArrayList<>()));

        when(repository.findByUsername("trainer1")).thenReturn(Optional.of(existing));

        TrainerWorkloadRequest request = buildRequest("trainer1", 2025, 8, 45, TrainerWorkloadRequest.ActionType.ADD);

        // when
        service.processWorkload(request, "tx-123");

        // then
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository).save(captor.capture());
        TrainerWorkload updated = captor.getValue();

        assertThat(updated.getYears().get(0).getMonths()).anyMatch(m -> m.getMonth() == 8 && m.getTotalDuration() == 45);
    }

    @Test
    void shouldSubtractDurationOnDelete() {
        // given
        TrainerWorkload existing = new TrainerWorkload("trainer1", "John", "Doe", true);
        existing.getYears().add(new com.gymcrm.trainerworkload.model.WorkloadYear(2025, new ArrayList<>()));
        existing.getYears().get(0).getMonths().add(new com.gymcrm.trainerworkload.model.WorkloadMonth(7, 60));

        when(repository.findByUsername("trainer1")).thenReturn(Optional.of(existing));

        TrainerWorkloadRequest request = buildRequest("trainer1", 2025, 7, 20, TrainerWorkloadRequest.ActionType.DELETE);

        // when
        service.processWorkload(request, "tx-123");

        // then
        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository).save(captor.capture());
        TrainerWorkload updated = captor.getValue();

        assertThat(updated.getYears().get(0).getMonths().get(0).getTotalDuration()).isEqualTo(40);
    }

    @Test
    void shouldReturnMonthlyDuration() {
        // given
        TrainerWorkload existing = new TrainerWorkload("trainer1", "John", "Doe", true);
        existing.getYears().add(new com.gymcrm.trainerworkload.model.WorkloadYear(2025, new ArrayList<>()));
        existing.getYears().get(0).getMonths().add(new com.gymcrm.trainerworkload.model.WorkloadMonth(7, 100));
        when(repository.findByUsername("trainer1")).thenReturn(Optional.of(existing));

        // when
        Optional<Integer> duration = service.getMonthlyDuration("trainer1", 2025, 7);

        // then
        assertThat(duration).isPresent().contains(100);
    }

    @Test
    void shouldReturnEmptyIfNoTrainerFound() {
        when(repository.findByUsername("trainer1")).thenReturn(Optional.empty());
        Optional<Integer> duration = service.getMonthlyDuration("trainer1", 2025, 7);
        assertThat(duration).isEmpty();
    }
}
