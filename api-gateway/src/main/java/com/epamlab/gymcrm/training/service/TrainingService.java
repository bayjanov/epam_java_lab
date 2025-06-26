package com.epamlab.gymcrm.training.service;

import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainee.repository.TraineeRepository;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainer.repository.TrainerRepository;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.epamlab.gymcrm.training.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private final TrainingRepository trainingRepository;

    @Autowired
    private final TraineeRepository traineeRepository;

    @Autowired
    private final TrainerRepository trainerRepository;


    @Autowired
    public TrainingService(
            TrainingRepository trainingRepository,
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository
    ) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public void createTraining(Training training) {
        validateTrainingFields(training);

        Trainee trainee = training.getTrainee();
        Trainer trainer = training.getTrainer();

        trainee = traineeRepository.findById(trainee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));
        trainer = trainerRepository.findById(trainer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
        }
        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
        }

        // Save all entities to ensure persistence
        traineeRepository.save(trainee);
        trainerRepository.save(trainer);
        trainingRepository.save(training);

        logger.info("Created training: {}", training);
    }


    public Training getTraining(Long id) {
        Optional<Training> training = trainingRepository.findById(id);
        training.ifPresentOrElse(
                t -> logger.info("Retrieved training: {}", t),
                () -> logger.warn("Training ID {} not found", id)
        );
        return training.orElse(null);
    }



    public List<Training> getTraineeTrainingsWithFilters(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, TrainingType trainingType) {
        return trainingRepository.findTraineeTrainingsWithFilters(
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType != null ? trainingType.name() : null
        );
    }

    public List<Training> getTrainerTrainingsWithFilters(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainingRepository.findTrainerTrainingsWithFilters(trainerUsername, fromDate, toDate, traineeName);
    }

    public void updateTraining(Training training) {
        validateTrainingFields(training);
        trainingRepository.save(training);
        logger.info("Updated training: {}", training);
    }

    public void deleteTrainingById(Long id) {
        Optional<Training> training = trainingRepository.findById(id);
        if (training.isPresent()) {
            trainingRepository.delete(training.get());
            logger.info("Deleted training: {}", training.get());
        } else {
            logger.warn("Delete failed - no training with ID {}", id);
        }
    }

    public List<Training> listAllTrainings() {
        logger.info("Listing all trainings");
        return trainingRepository.findAll();
    }

    private void validateTrainingFields(Training training) {
        if (training.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type is required.");
        }
        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date is required.");
        }
        if (training.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        if (training.getTrainee() == null) {
            throw new IllegalArgumentException("Trainee must be assigned to training.");
        }
        if (training.getTrainer() == null) {
            throw new IllegalArgumentException("Trainer must be assigned to training.");
        }
    }
}
