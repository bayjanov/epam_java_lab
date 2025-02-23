package com.epamlab.gymcrm.service.training;

import com.epamlab.gymcrm.dao.TrainingDao;
import com.epamlab.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public void createTraining(Training training) {
        trainingDao.create(training);
        logger.info("Created training: {}", training);
    }

    public Training getTraining(Long id) {
        Training training = trainingDao.read(id);
        if (training != null) {
            logger.info("Retrieved training: {}", training);
        } else {
            logger.warn("Training ID {} not found", id);
        }
        return training;
    }

    public void updateTraining(Training training) {
        trainingDao.update(training);
        logger.info("Updated training: {}", training);
    }

    public void deleteTraining(Long id) {
        Training training = trainingDao.read(id);
        if (training != null) {
            trainingDao.delete(id);
            logger.info("Deleted training: {}", training);
        } else {
            logger.warn("Delete failed - no training with ID {}", id);
        }
    }

    public Map<Long, Training> listAllTrainings() {
        logger.info("Listing all trainings");
        return trainingDao.listAll();
    }
}