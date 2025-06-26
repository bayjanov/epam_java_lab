package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.epamlab.gymcrm.training.repository.TrainingRepository;
import com.epamlab.gymcrm.training.service.TrainingService;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainee.repository.TraineeRepository;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainer.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TrainingService trainingService;

    private Training training;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "123 Main St");
        trainee.setId(1L);
        trainer = new Trainer("Jane", "Smith", "Yoga", true);
        trainer.setId(2L);
        training = new Training(trainer, trainee, "Morning Yoga", TrainingType.YOGA, LocalDate.now(), 60);
    }

    @Test
    void testCreateTraining_Success() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));
        training.getTrainee().setId(1L);
        training.getTrainer().setId(2L);

        trainingService.createTraining(training);

        verify(trainingRepository).save(training);
    }

    @Test
    void testCreateTraining_InvalidTrainee() {
        training.getTrainee().setId(1L);
        training.getTrainer().setId(2L);
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(training));
        assertTrue(ex.getMessage().contains("Trainee not found"));
    }

    @Test
    void testCreateTraining_InvalidTrainer() {
        training.getTrainee().setId(1L);
        training.getTrainer().setId(2L);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(training));
        assertTrue(ex.getMessage().contains("Trainer not found"));
    }

    @Test
    void testGetTraining_Exists() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        assertEquals(training, trainingService.getTraining(1L));
    }

    @Test
    void testGetTraining_NotFound() {
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());
        assertNull(trainingService.getTraining(999L));
    }

    @Test
    void testUpdateTraining() {
        trainingService.updateTraining(training);
        verify(trainingRepository).save(training);
    }

    @Test
    void testDeleteTraining_Exists() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        trainingService.deleteTrainingById(1L);
        verify(trainingRepository).delete(training);
    }

    @Test
    void testDeleteTraining_NotFound() {
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());
        trainingService.deleteTrainingById(999L);
        verify(trainingRepository, never()).delete(any());
    }

    @Test
    void testValidationFails_MissingType() {
        training.setTrainingType(null);
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(training));
    }

    @Test
    void testValidationFails_InvalidDuration() {
        training.setDurationMinutes(0);
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(training));
    }

    @Test
    void testListAllTrainings() {
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        List<Training> all = trainingService.listAllTrainings();
        assertEquals(1, all.size());
    }
}
