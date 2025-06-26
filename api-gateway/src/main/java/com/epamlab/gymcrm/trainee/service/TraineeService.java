package com.epamlab.gymcrm.trainee.service;

import com.epamlab.gymcrm.trainee.dto.TraineeRegistrationResponse;
import com.epamlab.gymcrm.trainee.repository.TraineeRepository;
import com.epamlab.gymcrm.trainer.repository.TrainerRepository;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.repository.TrainingRepository;
import com.epamlab.gymcrm.user.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UserService userProfileService;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository,
                          UserService userProfileService) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.userProfileService = userProfileService;
    }

    public boolean authenticateTrainee(String username, String password) {
        return traineeRepository.findByUsername(username)
                .map(t -> userProfileService.matchesRawPassword(password, t.getPassword()))
                .orElse(false);
    }

    @Transactional
    public TraineeRegistrationResponse createTrainee(Trainee trainee) {
        validateTraineeFields(trainee);

        String rawPassword = userProfileService.generateRawPassword(10);
        String encodedPassword = userProfileService.encodePassword(rawPassword);

        trainee.setUsername(userProfileService.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName()));
        trainee.setPassword(encodedPassword);

        traineeRepository.save(trainee);

        return new TraineeRegistrationResponse(trainee, rawPassword);
    }

    public Trainee getTraineeById(Long id) {
        return traineeRepository.findById(id).orElse(null);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeRepository.findByUsername(username).orElse(null);
    }

    public List<Trainee> listAllTrainees() {
        return traineeRepository.findAll();
    }

    public List<Trainer> getUnassignedTrainersForTrainee(String username) {
        Trainee t = getTraineeByUsername(username);
        return trainerRepository.findUnassignedTrainersForTrainee(t.getId());
    }

    @Transactional
    public void updateTrainee(String username, String password, Trainee updatedTrainee) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
        if (!trainee.getPassword().equals(password))
            throw new SecurityException("Authentication failed");

        applyTraineeUpdates(trainee, updatedTrainee);
        traineeRepository.save(trainee);
    }

    @Transactional
    public void updateTraineeTrainersList(String username, String password, List<Long> newTrainerIds) {
        Trainee trainee = getTraineeByUsername(username);
        if (!trainee.getPassword().equals(password))
            throw new SecurityException("Authentication failed");

        List<Trainer> newTrainers = trainerRepository.findAllById(newTrainerIds);
        trainee.setTrainers(new HashSet<>(newTrainers));
        traineeRepository.save(trainee);
    }

    @Transactional
    public void activateTrainee(String username, String password) {
        updateActiveState(username, password, true);
    }

    @Transactional
    public void deactivateTrainee(String username, String password) {
        updateActiveState(username, password, false);
    }

    @Transactional
    public void changeTraineePassword(String username, String newPassword) {
        Trainee trainee = getTraineeByUsername(username);
        trainee.setPassword(userProfileService.encodePassword(newPassword));
        traineeRepository.save(trainee);
    }

    @Transactional
    public void deleteTraineeByUsername(String username) {
        Trainee trainee = getTraineeByUsername(username);
        List<Training> trainings = trainingRepository.findByTrainee_Username(username);
        trainingRepository.deleteAll(trainings);
        traineeRepository.delete(trainee);
    }

    private void updateActiveState(String username, String password, boolean active) {
        Trainee trainee = getTraineeByUsername(username);
        if (!trainee.getPassword().equals(password))
            throw new SecurityException("Authentication failed");

        trainee.setActive(active);
        traineeRepository.save(trainee);
    }

    // Helpers
    private void validateTraineeFields(Trainee t) {
        if (t.getFirstName() == null || t.getFirstName().isBlank()) throw new IllegalArgumentException("First name required");
        if (t.getLastName() == null || t.getLastName().isBlank()) throw new IllegalArgumentException("Last name required");
        if (t.getDateOfBirth() == null) throw new IllegalArgumentException("DOB required");
        if (t.getAddress() == null || t.getAddress().isBlank()) throw new IllegalArgumentException("Address required");
    }

    private void applyTraineeUpdates(Trainee existing, Trainee updates) {
        existing.setFirstName(updates.getFirstName());
        existing.setLastName(updates.getLastName());
        existing.setDateOfBirth(updates.getDateOfBirth());
        existing.setAddress(updates.getAddress());
    }
}
