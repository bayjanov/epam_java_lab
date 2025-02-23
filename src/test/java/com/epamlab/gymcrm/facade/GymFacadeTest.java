package com.epamlab.gymcrm.facade;

import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.service.trainee.TraineeService;
import com.epamlab.gymcrm.service.trainer.TrainerService;
import com.epamlab.gymcrm.service.training.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void createTrainer_ShouldDelegateToService() {
        Trainer trainer = new Trainer("John", "Doe", "Yoga", true);
        gymFacade.createTrainer(trainer);
        verify(trainerService, times(1)).createTrainer(trainer);
    }

    @Test
    void listAllTrainers_ShouldDelegateToService() {
        gymFacade.listAllTrainers();
        verify(trainerService, times(1)).listAllTrainers();
    }

    @Test
    void createTraining_ShouldDelegateToService() {
        Training training = new Training();
        gymFacade.createTraining(training);
        verify(trainingService, times(1)).createTraining(training);
    }
}