package com.epamlab.gymcrm;

import com.epamlab.gymcrm.config.ApplicationConfig;
import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDateTime;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        GymFacade gymFacade = context.getBean(GymFacade.class);

        // List Preloaded Trainers
        logger.info("Preloaded Trainers:");
        Map<Long, Trainer> trainers = gymFacade.listAllTrainers();
        trainers.forEach((id, trainer) ->
                logger.info("Trainer {}: {}", id, trainer)
        );

        // List Preloaded Trainees
        logger.info("Preloaded Trainees:");
        Map<Long, Trainee> trainees = gymFacade.listAllTrainees();
        trainees.forEach((id, trainee) ->
                logger.info("Trainee {}: {}", id, trainee)
        );

        // Create Training Sessions
        if (!trainers.isEmpty() && !trainees.isEmpty()) {
            // Get first trainer and trainee from preloaded data
            Trainer firstTrainer = trainers.values().iterator().next();
            Trainee firstTrainee = trainees.values().iterator().next();

            Training training = new Training(
                    firstTrainer,
                    firstTrainee,
                    TrainingType.YOGA,
                    LocalDateTime.now(),
                    60
            );
            gymFacade.createTraining(training);
            logger.info("Created Training: {}", training);
        }

        // Delete First Trainee
        if (!trainees.isEmpty()) {
            Long firstTraineeId = trainees.keySet().iterator().next();
            logger.info("Deleting Trainee with ID: {}", firstTraineeId);
            gymFacade.deleteTrainee(firstTraineeId);
        }

        // Delete First Trainer
        if (!trainers.isEmpty()) {
            Long firstTrainerId = trainers.keySet().iterator().next();
            logger.info("Deleting Trainer with ID: {}", firstTrainerId);
            gymFacade.deleteTrainer(firstTrainerId);
        }

        // Final State After Deletion
        logger.info("Final Trainers:");
        gymFacade.listAllTrainers().forEach((id, trainer) ->
                logger.info("Trainer {}: {}", id, trainer)
        );

        logger.info("Final Trainees:");
        gymFacade.listAllTrainees().forEach((id, trainee) ->
                logger.info("Trainee {}: {}", id, trainee)
        );

        context.close();
    }
}