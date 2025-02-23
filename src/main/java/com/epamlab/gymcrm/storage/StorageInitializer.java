package com.epamlab.gymcrm.storage;

import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.service.user.UserProfileService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Map;

@Component
public class StorageInitializer {
    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${storage.file.path}")
    private String filePath;

    private final ResourceLoader resourceLoader;
    private final Map<Long, Trainer> trainerStorage;
    private final Map<Long, Trainee> traineeStorage;
    private final UserProfileService userProfileService;

    @Autowired
    public StorageInitializer(
            ResourceLoader resourceLoader,
            @Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage,
            @Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage,
            UserProfileService userProfileService
    ) {
        this.resourceLoader = resourceLoader;
        this.trainerStorage = trainerStorage;
        this.traineeStorage = traineeStorage;
        this.userProfileService = userProfileService;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing storage from: {}", filePath);
        loadInitialData();
    }

    private void loadInitialData() {
        try {
            Resource resource = resourceLoader.getResource(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    processLine(line);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to initialize storage from {}", filePath, e);
            throw new IllegalStateException("Storage initialization failed", e);
        }
    }

    private void processLine(String line) {
        String[] parts = line.split(",");
        try {
            if (parts[0].equalsIgnoreCase("trainer")) {
                createAndStoreTrainer(parts);
            } else if (parts[0].equalsIgnoreCase("trainee")) {
                createAndStoreTrainee(parts);
            }
        } catch (Exception e) {
            logger.warn("Skipping invalid data line: {}", line, e);
        }
    }

    private void createAndStoreTrainer(String[] parts) {
        Trainer trainer = new Trainer(
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                Boolean.parseBoolean(parts[4].trim())
        );

        // Generate credentials
        trainer.setUsername(userProfileService.generateUniqueUsername(
                trainer.getFirstName(),
                trainer.getLastName()
        ));
        trainer.setPassword(userProfileService.generatePassword(10));

        trainerStorage.put(trainer.getUserId(), trainer);
        logger.info("Stored trainer: {}", trainer);
    }

    private void createAndStoreTrainee(String[] parts) {
        Trainee trainee = new Trainee(
                parts[1].trim(),
                parts[2].trim(),
                Boolean.parseBoolean(parts[3].trim()),
                LocalDate.parse(parts[4].trim()),
                parts[5].trim()
        );

        // Generate credentials
        trainee.setUsername(userProfileService.generateUniqueUsername(
                trainee.getFirstName(),
                trainee.getLastName()
        ));
        trainee.setPassword(userProfileService.generatePassword(10));

        traineeStorage.put(trainee.getUserId(), trainee);
        logger.info("Stored trainee: {}", trainee);
    }
}