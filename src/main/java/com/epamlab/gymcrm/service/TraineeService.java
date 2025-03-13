package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TraineeDao;
import com.epamlab.gymcrm.dao.TrainerDao;
import com.epamlab.gymcrm.dao.TrainingDao;
import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.model.Training;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;
    private final UserProfileService userProfileService;

    @Autowired
    public TraineeService(TraineeDao traineeDao, TrainerDao trainerDao, TrainingDao trainingDao, UserProfileService userProfileService) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingDao = trainingDao;
        this.userProfileService = userProfileService;
    }

    // Authentication

    public boolean authenticateTrainee(String username, String password) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee == null) {
            logger.warn("Trainee not found with username: {}", username);
            return false;
        }
        boolean authenticated = trainee.getPassword().equals(password);
        if (authenticated) {
            logger.info("Trainee authenticated successfully: {}", username);
        } else {
            logger.warn("Password mismatch for trainee: {}", username);
        }
        return authenticated;
    }

    private void validateAuthentication(String username, String password) {
        if (!authenticateTrainee(username, password)) {
            throw new SecurityException("Authentication failed for trainee: " + username);
        }
    }

    // Creation

    @Transactional
    public void createTrainee(Trainee trainee) {
        validateTraineeFields(trainee);

        String username = userProfileService.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        trainee.setUsername(username);
        trainee.setPassword(userProfileService.generatePassword(10));

        traineeDao.save(trainee);
        logger.info("Created trainee: {}", trainee);
    }

    // Retrieval

    public Trainee getTraineeById(Long id) {
        Trainee trainee = traineeDao.findById(id);
        logIfNotFound(trainee, id);
        return trainee;
    }

    public Trainee getTraineeByUsername(String username) {
        Trainee trainee = traineeDao.findByUsername(username);
        return trainee;
    }

    public List<Trainee> listAllTrainees() {
        logger.info("Listing all trainees");
        return traineeDao.findAll();
    }

    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found with username: " + traineeUsername);
        }
        return trainerDao.findUnassignedTrainersForTrainee(trainee.getId());
    }

    // Update

    @Transactional
    public void updateTrainee(String username, String password, Trainee updatedTrainee) {
        validateAuthentication(username, password);
        validateTraineeFields(updatedTrainee);

        Trainee existingTrainee = getExistingTrainee(username);
        applyTraineeUpdates(existingTrainee, updatedTrainee);

        traineeDao.update(existingTrainee);
        logger.info("Updated trainee: {}", username);
    }

    @Transactional
    public void updateTraineeTrainersList(String username, String password, List<Long> newTrainerIds) {
        validateAuthentication(username, password);

        Trainee trainee = getExistingTrainee(username);
        List<Trainer> newTrainers = resolveTrainers(newTrainerIds);

        trainee.setTrainers(Set.copyOf(newTrainers));
        traineeDao.update(trainee);

        logger.info("Updated trainers list for trainee: {}", username);
    }

    // Activation / Deactivation

    @Transactional
    public void activateTrainee(String username, String password) {
        validateAuthentication(username, password);

        Trainee trainee = getExistingTrainee(username);
        if (!trainee.isActive()) {
            trainee.setActive(true);
            traineeDao.update(trainee);
            logger.info("Activated trainee: {}", username);
        } else {
            logger.warn("Trainee {} is already active.", username);
        }
    }

    @Transactional
    public void deactivateTrainee(String username, String password) {
        validateAuthentication(username, password);

        Trainee trainee = getExistingTrainee(username);
        if (trainee.isActive()) {
            trainee.setActive(false);
            traineeDao.update(trainee);
            logger.info("Deactivated trainee: {}", username);
        } else {
            logger.warn("Trainee {} is already inactive.", username);
        }
    }

    // Password Change

    @Transactional
    public void changeTraineePassword(String username, String newPassword) {
        Trainee trainee = getExistingTrainee(username);
        trainee.setPassword(newPassword);
        traineeDao.update(trainee);
        logger.info("Password changed for trainee: {}", username);
    }

    // Deletion

    @Transactional
    public void deleteTraineeByUsername(String username) {
        Trainee trainee = getExistingTrainee(username);

        List<Training> trainings = trainingDao.findByTraineeUsername(username);
        trainings.forEach(trainingDao::delete);

        traineeDao.delete(trainee);
        logger.info("Deleted trainee: {}", username);
    }

    // Helpers

    private void validateTraineeFields(Trainee trainee) {
        if (trainee.getFirstName() == null || trainee.getFirstName().isBlank())
            throw new IllegalArgumentException("First name is required.");
        if (trainee.getLastName() == null || trainee.getLastName().isBlank())
            throw new IllegalArgumentException("Last name is required.");
        if (trainee.getDateOfBirth() == null)
            throw new IllegalArgumentException("Date of birth is required.");
        if (trainee.getAddress() == null || trainee.getAddress().isBlank())
            throw new IllegalArgumentException("Address is required.");
    }

    private Trainee getExistingTrainee(String username) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found: " + username);
        }
        return trainee;
    }

    private List<Trainer> resolveTrainers(List<Long> trainerIds) {
        List<Trainer> trainers = trainerIds.stream()
                .map(trainerDao::findById)
                .filter(java.util.Objects::nonNull)
                .toList();

        if (trainers.size() != trainerIds.size()) {
            throw new IllegalArgumentException("One or more provided trainer IDs are invalid.");
        }
        return trainers;
    }

    private void applyTraineeUpdates(Trainee existing, Trainee updates) {
        existing.setFirstName(updates.getFirstName());
        existing.setLastName(updates.getLastName());
        existing.setDateOfBirth(updates.getDateOfBirth());
        existing.setAddress(updates.getAddress());
    }

    private void logIfNotFound(Trainee trainee, Long id) {
        if (trainee == null) {
            logger.warn("Trainee ID {} not found", id);
        }
    }
}
