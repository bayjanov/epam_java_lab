package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TrainingDao;
import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;

    @Autowired
    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public void createTraining(Training training) {
        validateTrainingFields(training);
        trainingDao.save(training);
        logger.info("Created training: {}", training);
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


    public Training getTraining(Long id) {
        Training training = trainingDao.findById(id);
        if (training != null) {
            logger.info("Retrieved training: {}", training);
        } else {
            logger.warn("Training ID {} not found", id);
        }
        return training;
    }

    public List<Training> getTraineeTrainingsWithFilters(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, TrainingType trainingType) {
        return trainingDao.findTraineeTrainingsWithFilters(traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

    public List<Training> getTrainerTrainingsWithFilters(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainingDao.findTrainerTrainingsWithFilters(trainerUsername, fromDate, toDate, traineeName);
    }


    public void updateTraining(Training training) {
        trainingDao.update(training);
        logger.info("Updated training: {}", training);
    }

    public void deleteTraining(Long id) {
        Training training = trainingDao.findById(id);
        if (training != null) {
            trainingDao.delete(training);
            logger.info("Deleted training: {}", training);
        } else {
            logger.warn("Delete failed - no training with ID {}", id);
        }
    }



    public List<Training> listAllTrainings() {
        logger.info("Listing all trainings");
        return trainingDao.findAll();
    }
}