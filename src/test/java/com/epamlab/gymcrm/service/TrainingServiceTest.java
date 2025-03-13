package com.epamlab.gymcrm.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.epamlab.gymcrm.dao.TrainingDao;
import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.model.TrainingType;
import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService trainingService;

    private Training validTraining;
    private Trainee testTrainee;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "123 Main St");
        testTrainer = new Trainer("Trainer", "One", "Cardio", true);

        validTraining = new Training(testTrainer, testTrainee, "Morning Yoga",TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60);
        validTraining.setTrainingType(TrainingType.CARDIO);
        validTraining.setTrainingDate(LocalDate.now());
        validTraining.setDurationMinutes(60);
        validTraining.setTrainee(testTrainee);
        validTraining.setTrainer(testTrainer);
    }

    @Test
    void testCreateTraining_ValidData() {
        trainingService.createTraining(validTraining);
        verify(trainingDao).save(validTraining);
    }

    @Test
    void testCreateTraining_MissingType() {
        validTraining.setTrainingType(null);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(validTraining));
    }

    @Test
    void testCreateTraining_MissingDate() {
        validTraining.setTrainingDate(null);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(validTraining));
    }

    @Test
    void testCreateTraining_InvalidDuration() {
        validTraining.setDurationMinutes(0);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(validTraining));
    }

    @Test
    void testCreateTraining_MissingTrainee() {
        validTraining.setTrainee(null);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(validTraining));
    }

    @Test
    void testGetTraineeTrainingsWithFilters() {
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        String trainerName = "Smith";
        TrainingType type = TrainingType.YOGA;

        trainingService.getTraineeTrainingsWithFilters(
                "trainee.user", fromDate, toDate, trainerName, type);

        verify(trainingDao).findTraineeTrainingsWithFilters(
                eq("trainee.user"), eq(fromDate), eq(toDate),
                eq(trainerName), eq(type));
    }

    @Test
    void testGetTrainerTrainingsWithFilters() {
        LocalDate fromDate = LocalDate.of(2024, 6, 1);
        LocalDate toDate = LocalDate.of(2024, 6, 30);
        String traineeName = "Doe";

        trainingService.getTrainerTrainingsWithFilters(
                "trainer.user", fromDate, toDate, traineeName);

        verify(trainingDao).findTrainerTrainingsWithFilters(
                eq("trainer.user"), eq(fromDate), eq(toDate),
                eq(traineeName));
    }

    @Test
    void testGetTraining_Existing() {
        when(trainingDao.findById(1L)).thenReturn(validTraining);
        Training result = trainingService.getTraining(1L);
        assertNotNull(result);
        verify(trainingDao).findById(1L);
    }

    @Test
    void testGetTraining_NonExisting() {
        when(trainingDao.findById(999L)).thenReturn(null);
        Training result = trainingService.getTraining(999L);
        assertNull(result);
    }

    // Update tests
    @Test
    void testUpdateTraining() {
        Trainer trainer = new Trainer("Trainer", "One", "Cardio", true);
        Trainee trainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "123 Main St");

        Training updated = new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60);

        updated.setId(1L);
        updated.setDurationMinutes(90);

        trainingService.updateTraining(updated);
        verify(trainingDao).update(updated);
    }

    // Delete tests
    @Test
    void testDeleteTraining_Existing() {
        when(trainingDao.findById(1L)).thenReturn(validTraining);
        trainingService.deleteTraining(1L);
        verify(trainingDao).delete(validTraining);
    }

    @Test
    void testDeleteTraining_NonExisting() {
        when(trainingDao.findById(999L)).thenReturn(null);
        trainingService.deleteTraining(999L);
        verify(trainingDao, never()).delete(any());
    }
}