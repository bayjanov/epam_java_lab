package com.epamlab.gymcrm.facade;

import com.epamlab.gymcrm.client.WorkloadGateway;
import com.epamlab.gymcrm.client.dto.TrainerWorkloadRequest;
import com.epamlab.gymcrm.trainee.dto.TraineeRegistrationResponse;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainer.dto.TrainerRegistrationResponse;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.epamlab.gymcrm.trainee.service.TraineeService;
import com.epamlab.gymcrm.trainer.service.TrainerService;
import com.epamlab.gymcrm.training.service.TrainingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
@Transactional
public class GymFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final WorkloadGateway workloadGateway;


    // Trainer Management
    @Transactional
    public TrainerRegistrationResponse createTrainer(Trainer trainer) {
        return trainerService.createTrainer(trainer);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    @Transactional
    public void updateTrainer(String username, String password, Trainer updated) {
        trainerService.updateTrainer(username, password, updated);
    }

    // NEW: List all trainers
    public List<Trainer> listAllTrainers() {
        return trainerService.listAllTrainers();
    }

    // Trainee Management
    @Transactional
    public TraineeRegistrationResponse createTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    @Transactional
    public void updateTrainee(String username, String password, Trainee updated) {
        traineeService.updateTrainee(username, password, updated);
    }

    // List all trainees
    public List<Trainee> listAllTrainees() {
        return traineeService.listAllTrainees();
    }

    // Authentication
    public boolean authenticateTrainer(String username, String password) {
        return trainerService.authenticateTrainer(username, password);
    }

    public boolean authenticateTrainee(String username, String password) {
        return traineeService.authenticateTrainee(username, password);
    }

    // Activation Management
    @Transactional
    public void activateTrainer(String username, String password) {
        trainerService.activateTrainer(username, password);
    }

    @Transactional
    public void deactivateTrainer(String username, String password) {
        trainerService.deactivateTrainer(username, password);
    }

    @Transactional
    public void activateTrainee(String username, String password) {
        traineeService.activateTrainee(username, password);
    }

    @Transactional
    public void deactivateTrainee(String username, String password) {
        traineeService.deactivateTrainee(username, password);
    }

    // Training Management
    @Transactional
    public Training addTraining(Training training) {
        trainingService.createTraining(training);

        workloadGateway.send(new TrainerWorkloadRequest(
                training.getTrainer().getUsername(),
                training.getTrainer().getFirstName(),
                training.getTrainer().getLastName(),
                training.getTrainer().isActive(),
                training.getTrainingDate(),
                training.getDurationMinutes(),
                TrainerWorkloadRequest.ActionType.ADD
        ));

        return training;
    }

    @Transactional
    public void cancelTraining(Long trainingId) {


        Training t = trainingService.getTraining(trainingId);   // implement in service
        workloadGateway.send( new TrainerWorkloadRequest(
                t.getTrainer().getUsername(), t.getTrainer().getFirstName(),
                t.getTrainer().getLastName(), t.getTrainer().isActive(),
                t.getTrainingDate(), t.getDurationMinutes(),
                TrainerWorkloadRequest.ActionType.DELETE));

        trainingService.deleteTrainingById(trainingId);
    }


    public List<Training> getTraineeTrainings(String username, LocalDate from, LocalDate to,
                                              String trainerName, TrainingType type) {
        return trainingService.getTraineeTrainingsWithFilters(username, from, to, trainerName, type);
    }

    public List<Training> getTrainerTrainings(String username, LocalDate from, LocalDate to,
                                              String traineeName) {
        return trainingService.getTrainerTrainingsWithFilters(username, from, to, traineeName);
    }

    // Many-to-Many Management
    @Transactional
    public void updateTraineeTrainers(String username, String password, List<Long> trainerIds) {
        traineeService.updateTraineeTrainersList(username, password, trainerIds);
    }

    public List<Trainer> getAvailableTrainersForTrainee(String username) {
        return traineeService.getUnassignedTrainersForTrainee(username);
    }

    // Deletion
    @Transactional
    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }
}
