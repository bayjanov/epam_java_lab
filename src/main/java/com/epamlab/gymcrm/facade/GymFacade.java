package com.epamlab.gymcrm.facade;

import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.service.trainee.TraineeService;
import com.epamlab.gymcrm.service.trainer.TrainerService;
import com.epamlab.gymcrm.service.training.TrainingService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GymFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public GymFacade(TrainerService trainerService, TraineeService traineeService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    // Trainer operations
    public void createTrainer(Trainer trainer) {
        trainerService.createTrainer(trainer);
    }

    public Map<Long, Trainer> listAllTrainers() {
        return trainerService.listAllTrainers();
    }

    // Trainee operations
    public void createTrainee(Trainee trainee) {
        traineeService.createTrainee(trainee);
    }

    public Map<Long, Trainee> listAllTrainees() {
        return traineeService.listAllTrainees();
    }

    // Training operations
    public void createTraining(Training training) {
        trainingService.createTraining(training);
    }

    public Map<Long, Training> listAllTrainings() {
        return trainingService.listAllTrainings();
    }

    public void deleteTrainee(Long id) {
        traineeService.deleteTrainee(id);
    }

    public void deleteTrainer(Long id) {
        traineeService.deleteTrainee(id);
    }
}
