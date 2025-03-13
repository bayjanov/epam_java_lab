package com.epamlab.gymcrm.facade;

import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainee.service.TraineeService;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainer.service.TrainerService;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.epamlab.gymcrm.training.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setup() {
        trainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "123 Street");
        trainee.setUsername("john.doe");
        trainee.setPassword("password123");

        trainer = new Trainer("Jane", "Smith", "Cardio", true);
        trainer.setUsername("jane.smith");

        training = new Training(trainer, trainee, "Morning Yoga", TrainingType.YOGA, LocalDate.now(), 60);
    }

    @Test
    void createTrainer_ShouldCallService() {
        gymFacade.createTrainer(trainer);
        verify(trainerService).createTrainer(trainer);
    }

    @Test
    void getTrainerByUsername_ShouldReturnTrainer() {
        when(trainerService.getTrainerByUsername("jane.smith")).thenReturn(trainer);
        Trainer result = gymFacade.getTrainerByUsername("jane.smith");
        assertEquals(trainer, result);
    }

    @Test
    void updateTrainer_ShouldCallService() {
        gymFacade.updateTrainer("jane.smith", "password123", trainer);
        verify(trainerService).updateTrainer("jane.smith", "password123", trainer);
    }

    @Test
    void listAllTrainers_ShouldReturnList() {
        when(trainerService.listAllTrainers()).thenReturn(Collections.singletonList(trainer));
        List<Trainer> result = gymFacade.listAllTrainers();
        assertEquals(1, result.size());
        assertEquals(trainer, result.getFirst());
    }

    @Test
    void createTrainee_ShouldCallService() {
        gymFacade.createTrainee(trainee);
        verify(traineeService).createTrainee(trainee);
    }

    @Test
    void getTraineeByUsername_ShouldReturnTrainee() {
        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
        Trainee result = gymFacade.getTraineeByUsername("john.doe");
        assertEquals(trainee, result);
    }

    @Test
    void updateTrainee_ShouldCallService() {
        gymFacade.updateTrainee("john.doe", "password123", trainee);
        verify(traineeService).updateTrainee("john.doe", "password123", trainee);
    }

    @Test
    void listAllTrainees_ShouldReturnList() {
        when(traineeService.listAllTrainees()).thenReturn(Collections.singletonList(trainee));
        List<Trainee> result = gymFacade.listAllTrainees();
        assertEquals(1, result.size());
        assertEquals(trainee, result.getFirst());
    }

    @Test
    void authenticateTrainer_ShouldCallService() {
        when(trainerService.authenticateTrainer("jane.smith", "password123")).thenReturn(true);
        assertTrue(gymFacade.authenticateTrainer("jane.smith", "password123"));
    }

    @Test
    void authenticateTrainee_ShouldCallService() {
        when(traineeService.authenticateTrainee("john.doe", "password123")).thenReturn(true);
        assertTrue(gymFacade.authenticateTrainee("john.doe", "password123"));
    }

    @Test
    void activateTrainer_ShouldCallService() {
        gymFacade.activateTrainer("jane.smith", "password123");
        verify(trainerService).activateTrainer("jane.smith", "password123");
    }

    @Test
    void deactivateTrainer_ShouldCallService() {
        gymFacade.deactivateTrainer("jane.smith", "password123");
        verify(trainerService).deactivateTrainer("jane.smith", "password123");
    }

    @Test
    void activateTrainee_ShouldCallService() {
        gymFacade.activateTrainee("john.doe", "password123");
        verify(traineeService).activateTrainee("john.doe", "password123");
    }

    @Test
    void deactivateTrainee_ShouldCallService() {
        gymFacade.deactivateTrainee("john.doe", "password123");
        verify(traineeService).deactivateTrainee("john.doe", "password123");
    }

    @Test
    void addTraining_ShouldCallService() {
        gymFacade.addTraining(training);
        verify(trainingService).createTraining(training);
    }

    @Test
    void getTraineeTrainings_ShouldReturnList() {
        when(trainingService.getTraineeTrainingsWithFilters("john.doe", null, null, null, null))
                .thenReturn(Collections.singletonList(training));
        List<Training> result = gymFacade.getTraineeTrainings("john.doe", null, null, null, null);
        assertEquals(1, result.size());
        assertEquals(training, result.getFirst());
    }

    @Test
    void getTrainerTrainings_ShouldReturnList() {
        when(trainingService.getTrainerTrainingsWithFilters("jane.smith", null, null, null))
                .thenReturn(Collections.singletonList(training));
        List<Training> result = gymFacade.getTrainerTrainings("jane.smith", null, null, null);
        assertEquals(1, result.size());
        assertEquals(training, result.getFirst());
    }

    @Test
    void updateTraineeTrainers_ShouldCallService() {
        gymFacade.updateTraineeTrainers("john.doe", "password123", List.of(1L));
        verify(traineeService).updateTraineeTrainersList("john.doe", "password123", List.of(1L));
    }

    @Test
    void getAvailableTrainersForTrainee_ShouldReturnList() {
        when(traineeService.getUnassignedTrainersForTrainee("john.doe"))
                .thenReturn(Collections.singletonList(trainer));
        List<Trainer> result = gymFacade.getAvailableTrainersForTrainee("john.doe");
        assertEquals(1, result.size());
        assertEquals(trainer, result.getFirst());
    }

    @Test
    void deleteTraineeByUsername_ShouldCallService() {
        gymFacade.deleteTraineeByUsername("john.doe");
        verify(traineeService).deleteTraineeByUsername("john.doe");
    }
}
