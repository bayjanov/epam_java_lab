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

import java.time.LocalDate;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // Bootstrap Spring
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ApplicationConfig.class);

        // Obtain  facade bean
        GymFacade gymFacade = context.getBean(GymFacade.class);

        // CREATE AND PERSIST SAMPLE TRAINERS
        Trainer trainer1 = new Trainer("Alice", "Brown", "Yoga", true);
        Trainer trainer2 = new Trainer("Bob", "Green", "Strength", true);
        gymFacade.createTrainer(trainer1);
        gymFacade.createTrainer(trainer2);

        // 3. List all trainers
        logger.info("\n=== LISTING TRAINERS ===");
        List<Trainer> allTrainers = gymFacade.listAllTrainers();
        allTrainers.forEach(t -> logger.info("Trainer: {}", t));

        // CREATE AND PERSIST SAMPLE TRAINEES
        Trainee trainee1 = new Trainee("Charlie", "White", true,
                LocalDate.of(1985, 5, 20), "123 Main St");
        Trainee trainee2 = new Trainee("Diana", "Black", true,
                LocalDate.of(1990, 8, 15), "456 Oak Ave");
        gymFacade.createTrainee(trainee1);
        gymFacade.createTrainee(trainee2);

        // 4. List all trainees
        logger.info("\n=== LISTING TRAINEES ===");
        List<Trainee> allTrainees = gymFacade.listAllTrainees();
        allTrainees.forEach(t -> logger.info("Trainee: {}", t));

        // CREATE A TRAINING SESSION
        if (!allTrainers.isEmpty() && !allTrainees.isEmpty()) {
            Trainer firstTrainer = allTrainers.get(0);
            Trainee firstTrainee = allTrainees.get(0);

            Training training = new Training(
                    firstTrainer,
                    firstTrainee,
                    "Morning Yoga",
                    TrainingType.YOGA,
                    LocalDate.now(),
                    60
            );
            gymFacade.addTraining(training);
            logger.info("\nCreated Training: {}", training);
        }

        // FINAL STATE
        logger.info("\n=== FINAL TRAINERS ===");
        gymFacade.listAllTrainers().forEach(trainer ->
                logger.info("Trainer: {}", trainer)
        );

        logger.info("\n=== FINAL TRAINEES ===");
        gymFacade.listAllTrainees().forEach(trainee ->
                logger.info("Trainee: {}", trainee)
        );

        // Close Spring context
        context.close();
    }
}
