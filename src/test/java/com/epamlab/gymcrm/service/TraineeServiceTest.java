package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TraineeDao;
import com.epamlab.gymcrm.dao.TrainerDao;
import com.epamlab.gymcrm.dao.TrainingDao;
import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        // trainee
        trainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "123 Main St");
        trainee.setUsername("john.doe");
        trainee.setPassword("password123");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setTrainers(new HashSet<>());
        trainee.setActive(true);

        // trainer
        trainer = new Trainer("Trainer", "One", "Cardio", true);
        trainer.setId(1L);
        trainer.setFirstName("Trainer");
        trainer.setLastName("One");
        trainer.setSpecialization("Cardio");
    }

    @Test
    void createTrainee_ShouldGenerateCredentialsAndSave() {
        when(userProfileService.generateUniqueUsername("John", "Doe")).thenReturn("john.doe");
        when(userProfileService.generatePassword(10)).thenReturn("password123");

        traineeService.createTrainee(trainee);

        assertEquals("john.doe", trainee.getUsername());
        assertEquals("password123", trainee.getPassword());
        verify(traineeDao).save(trainee);
    }

    @Test
    void authenticateTrainee_ShouldReturnTrue_WhenPasswordMatches() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);
        assertTrue(traineeService.authenticateTrainee("john.doe", "password123"));
    }

    @Test
    void authenticateTrainee_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);
        assertFalse(traineeService.authenticateTrainee("john.doe", "wrongPassword"));
    }

    @Test
    void getTrainee_ShouldReturnTrainee_WhenExists() {
        when(traineeDao.findById(1L)).thenReturn(trainee);
        Trainee result = traineeService.getTraineeById(1L);
        assertEquals("john.doe", result.getUsername());
    }

    @Test
    void updateTrainee_ShouldUpdateFieldsAndSave() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);

        Trainee updatedTrainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "123 Main St");
        updatedTrainee.setFirstName("Updated");
        updatedTrainee.setLastName("Cavill");
        updatedTrainee.setDateOfBirth(LocalDate.of(1993, 3, 7));
        updatedTrainee.setAddress("New Address");

        traineeService.updateTrainee("john.doe", "password123", updatedTrainee);

        assertEquals("Updated", trainee.getFirstName());
        assertEquals("New Address", trainee.getAddress());
        verify(traineeDao).update(trainee);
    }

    @Test
    void changeTraineePassword_ShouldUpdatePassword() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);

        traineeService.changeTraineePassword("john.doe", "newPassword123");

        assertEquals("newPassword123", trainee.getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    void activateTrainee_ShouldActivate() {
        trainee.setActive(false);
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);

        traineeService.activateTrainee("john.doe", "password123");

        assertTrue(trainee.isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void deactivateTrainee_ShouldDeactivate() {
        trainee.setActive(true);
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);

        traineeService.deactivateTrainee("john.doe", "password123");

        assertFalse(trainee.isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void deleteTraineeByUsername_ShouldCascadeDeleteTrainings() {
        trainee.setUsername("john.doe");
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);

        // trainings that belong to the same trainee.
        Training training1 = new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60);
        Training training2 = new Training(trainer, trainee, "Morning Yoga", TrainingType.CARDIO, LocalDate.of(2024, 3, 8), 45);

        when(trainingDao.findByTraineeUsername("john.doe")).thenReturn(List.of(training1, training2));

        traineeService.deleteTraineeByUsername("john.doe");

        verify(trainingDao).delete(training1);
        verify(trainingDao).delete(training2);
        verify(traineeDao).delete(trainee);
    }

    @Test
    void getUnassignedTrainersForTrainee_ShouldReturnList() {
        trainee.setId(1L);
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);
        when(trainerDao.findUnassignedTrainersForTrainee(1L)).thenReturn(List.of(trainer));

        List<Trainer> result = traineeService.getUnassignedTrainersForTrainee("john.doe");

        assertEquals(1, result.size());
        assertEquals(trainer.getFirstName(), result.get(0).getFirstName());
    }

    @Test
    void updateTraineeTrainersList_ShouldUpdateTrainers() {
        trainee.setId(1L);
        when(traineeDao.findByUsername("john.doe")).thenReturn(trainee);
        when(trainerDao.findById(1L)).thenReturn(trainer);

        traineeService.updateTraineeTrainersList("john.doe", "password123", List.of(1L));

        assertTrue(trainee.getTrainers().contains(trainer));
        verify(traineeDao).update(trainee);
    }

    @Test
    void listAllTrainees_ShouldReturnAllTrainees() {
        when(traineeDao.findAll()).thenReturn(List.of(trainee));

        List<Trainee> result = traineeService.listAllTrainees();

        assertEquals(1, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
    }
}
