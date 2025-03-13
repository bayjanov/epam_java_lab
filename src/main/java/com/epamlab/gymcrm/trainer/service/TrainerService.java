package com.epamlab.gymcrm.trainer.service;

import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainer.repository.TrainerRepository;
import com.epamlab.gymcrm.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final UserService userProfileService;

    public TrainerService(TrainerRepository trainerRepository, UserService userProfileService) {
        this.trainerRepository = trainerRepository;
        this.userProfileService = userProfileService;
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerRepository.findByUsername(username)
                .map(t -> t.getPassword().equals(password))
                .orElse(false);
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
        trainerRepository.save(trainer);
        logger.info("Created trainer: {}", trainer);
    }

    public Trainer getTrainer(Long id) {
        return trainerRepository.findById(id).orElse(null);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerRepository.findByUsername(username).orElse(null);
    }

    public void updateTrainer(String username, String password, Trainer updatedTrainer) {
        validateAuthentication(username, password);
        validateTrainerFields(updatedTrainer);

        Trainer existingTrainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));

        existingTrainer.setFirstName(updatedTrainer.getFirstName());
        existingTrainer.setLastName(updatedTrainer.getLastName());
        existingTrainer.setTrainees(updatedTrainer.getTrainees());

        trainerRepository.save(existingTrainer);
        logger.info("Updated trainer: {}", username);
    }

    public void activateTrainer(String username, String password) {
        validateAuthentication(username, password);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));

        trainer.setActive(true);
        trainerRepository.save(trainer);
        logger.info("Activated trainer: {}", username);
    }

    public void deactivateTrainer(String username, String password) {
        validateAuthentication(username, password);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));

        trainer.setActive(false);
        trainerRepository.save(trainer);
        logger.info("Deactivated trainer: {}", username);
    }

    public void changeTrainerPassword(String username, String newPassword) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));

        trainer.setPassword(newPassword);
        trainerRepository.save(trainer);
        logger.info("Password changed for trainer: {}", username);
    }

    public List<Trainer> listAllTrainers() {
        logger.info("Listing all trainers");
        return trainerRepository.findAll();
    }
}
