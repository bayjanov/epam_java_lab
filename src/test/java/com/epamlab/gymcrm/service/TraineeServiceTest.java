package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainee.repository.TraineeRepository;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainer.repository.TrainerRepository;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.epamlab.gymcrm.trainee.service.TraineeService;
import com.epamlab.gymcrm.training.repository.TrainingRepository;
import com.epamlab.gymcrm.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingRepository trainingRepository;
    @Mock private UserService userService;

    @InjectMocks private TraineeService traineeService;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "123 Main St");
        trainee.setUsername("john.doe");
        trainee.setPassword("password123");
        trainer = new Trainer("Trainer", "One", "Cardio", true);
        trainer.setId(1L);
    }

    @Test
    void createTrainee_shouldGenerateCredentialsAndSave() {
        when(userService.generateUniqueUsername("John", "Doe")).thenReturn("john.doe");
        when(userService.generatePassword(10)).thenReturn("password123");

        traineeService.createTrainee(trainee);

        verify(traineeRepository).save(trainee);
        assertEquals("john.doe", trainee.getUsername());
    }

    @Test
    void authenticateTrainee_shouldReturnTrue() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        assertTrue(traineeService.authenticateTrainee("john.doe", "password123"));
    }

    @Test
    void updateTrainee_shouldApplyUpdatesAndSave() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        Trainee updated = new Trainee("Updated", "Name", true, LocalDate.of(1991, 2, 2), "New Address");

        traineeService.updateTrainee("john.doe", "password123", updated);

        verify(traineeRepository).save(trainee);
        assertEquals("Updated", trainee.getFirstName());
        assertEquals("New Address", trainee.getAddress());
    }

    @Test
    void deleteTrainee_shouldCascadeDeleteTrainingsAndDeleteTrainee() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        Training t1 = new Training(trainer, trainee, "Yoga", TrainingType.YOGA, LocalDate.now(), 60);
        Training t2 = new Training(trainer, trainee, "Boxing", TrainingType.CARDIO, LocalDate.now(), 45);

        when(trainingRepository.findByTrainee_Username("john.doe")).thenReturn(List.of(t1, t2));

        traineeService.deleteTraineeByUsername("john.doe");

        verify(trainingRepository).deleteAll(List.of(t1, t2));
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void updateTrainerList_shouldSetNewTrainers() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllById(List.of(1L))).thenReturn(List.of(trainer));

        traineeService.updateTraineeTrainersList("john.doe", "password123", List.of(1L));

        verify(traineeRepository).save(trainee);
        assertTrue(trainee.getTrainers().contains(trainer));
    }

    @Test
    void activateTrainee_shouldEnableIsActive() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        trainee.setActive(false);

        traineeService.activateTrainee("john.doe", "password123");

        verify(traineeRepository).save(trainee);
        assertTrue(trainee.isActive());
    }

    @Test
    void deactivateTrainee_shouldDisableIsActive() {
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        trainee.setActive(true);

        traineeService.deactivateTrainee("john.doe", "password123");

        verify(traineeRepository).save(trainee);
        assertFalse(trainee.isActive());
    }

    @Test
    void getUnassignedTrainers_shouldQueryRepo() {
        trainee.setId(1L);
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findUnassignedTrainersForTrainee(1L)).thenReturn(List.of(trainer));

        List<Trainer> result = traineeService.getUnassignedTrainersForTrainee("john.doe");

        assertEquals(1, result.size());
        assertEquals(trainer.getFirstName(), result.getFirst().getFirstName());
    }
}
