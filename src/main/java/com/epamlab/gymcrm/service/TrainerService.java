package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TrainerDao;
import com.epamlab.gymcrm.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerDao trainerDao;
    private final UserProfileService userProfileService;

    @Autowired
    public TrainerService(TrainerDao trainerDao, UserProfileService userProfileService) {
        this.trainerDao = trainerDao;
        this.userProfileService = userProfileService;
    }

    public boolean authenticateTrainer(String username, String password) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            logger.warn("Trainer not found with username: {}", username);
            return false;
        }

        if (trainer.getPassword().equals(password)) {
            logger.info("Trainer authenticated successfully: {}", username);
            return true;
        } else {
            logger.warn("Password mismatch for trainer: {}", username);
            return false;
        }
    }

    private void validateAuthentication(String username, String password) {
        if (!authenticateTrainer(username, password)) {
            throw new SecurityException("Authentication failed for trainer: " + username);
        }
    }

    private void validateTrainerFields(Trainer trainer) {
        if (trainer.getFirstName() == null || trainer.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (trainer.getLastName() == null || trainer.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (trainer.getSpecialization() == null || trainer.getSpecialization().isBlank()) {
            throw new IllegalArgumentException("Specialization is required.");
        }
    }

    public void createTrainer(Trainer trainer) {
        trainer.setUsername(userProfileService.generateUniqueUsername(trainer.getFirstName(), trainer.getLastName()));
        trainer.setPassword(userProfileService.generatePassword(10));
        validateTrainerFields(trainer);
        trainerDao.save(trainer);
        logger.info("Created trainer: {}", trainer);
    }

    public Trainer getTrainer(Long id) {
        Trainer trainer = trainerDao.findById(id);
        if (trainer != null) {
            logger.info("Retrieved trainer: {}", trainer);
        } else {
            logger.warn("Trainer ID {} not found", id);
        }
        return trainer;
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerDao.findByUsername(username);
    }

    public void updateTrainer(String username, String password, Trainer updatedTrainer) {
        validateAuthentication(username, password);
        validateTrainerFields(updatedTrainer);

        Trainer existingTrainer = trainerDao.findByUsername(username);
        if (existingTrainer == null) {
            throw new IllegalArgumentException("Trainer not found: " + username);
        }

        existingTrainer.setFirstName(updatedTrainer.getFirstName());
        existingTrainer.setLastName(updatedTrainer.getLastName());
        existingTrainer.setSpecialization(updatedTrainer.getSpecialization());
        existingTrainer.setTrainees(updatedTrainer.getTrainees());

        trainerDao.update(existingTrainer);
        logger.info("Updated trainer: {}", username);
    }

    public void activateTrainer(String username, String password) {
        validateAuthentication(username, password);
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found: " + username);
        }

        trainer.setActive(true);
        trainerDao.update(trainer);
        logger.info("Activated trainer: {}", username);
    }

    public void deactivateTrainer(String username, String password) {
        validateAuthentication(username, password);
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found: " + username);
        }

        // Always set inactive and update.
        trainer.setActive(false);
        trainerDao.update(trainer);
        logger.info("Deactivated trainer: {}", username);
    }


    public void changeTrainerPassword(String username, String newPassword) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found: " + username);
        }
        trainer.setPassword(newPassword);
        trainerDao.update(trainer);
        logger.info("Password changed for trainer: {}", username);
    }

    public List<Trainer> listAllTrainers() {
        logger.info("Listing all trainers");
        return trainerDao.findAll();
    }
}
