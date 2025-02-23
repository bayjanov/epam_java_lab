package com.epamlab.gymcrm.service.trainer;

import com.epamlab.gymcrm.dao.TrainerDao;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.service.user.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private TrainerDao trainerDao;
    private UserProfileService userProfileService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public void createTrainer(Trainer trainer) {
        trainer.setUsername(userProfileService.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName()));
        trainer.setPassword(userProfileService.generatePassword(10));
        trainerDao.create(trainer);
        logger.info("Created trainer: {}", trainer);
    }

    public Trainer getTrainer(Long id) {
        Trainer trainer = trainerDao.read(id);
        if (trainer != null) {
            logger.info("Retrieved trainer: {}", trainer);
        } else {
            logger.warn("Trainer ID {} not found", id);
        }
        return trainer;
    }

    public void updateTrainer(Trainer trainer) {
        trainerDao.update(trainer);
        logger.info("Updated trainer: {}", trainer);
    }

    public void deleteTrainer(Long id) {
        Trainer trainer = trainerDao.read(id);
        if (trainer != null) {
            trainerDao.delete(id);
            logger.info("Deleted trainer: {}", trainer);
        } else {
            logger.warn("Delete failed - no trainer with ID {}", id);
        }
    }

    public Map<Long, Trainer> listAllTrainers() {
        logger.info("Listing all trainers");
        return trainerDao.listAll();
    }
}