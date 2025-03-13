package com.epamlab.gymcrm.seed;

import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final GymFacade gymFacade;

    public DataSeeder(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Override
    public void run(String... args) throws Exception {
        // CREATE TRAINERS
        Trainer trainer1 = new Trainer("Alice", "Brown", "Yoga", true);
        Trainer trainer2 = new Trainer("Bob", "Green", "Strength", true);
        gymFacade.createTrainer(trainer1);
        gymFacade.createTrainer(trainer2);

        // LIST TRAINERS
        System.out.println("\n=== LISTING TRAINERS ===");
        gymFacade.listAllTrainers().forEach(t -> System.out.println("Trainer: " + t));

        // CREATE TRAINEES
        Trainee trainee1 = new Trainee("Charlie", "White", true,
                LocalDate.of(1985, 5, 20), "123 Main St");
        Trainee trainee2 = new Trainee("Diana", "Black", true,
                LocalDate.of(1990, 8, 15), "456 Oak Ave");
        gymFacade.createTrainee(trainee1);
        gymFacade.createTrainee(trainee2);

        // LIST TRAINEES
        System.out.println("\n=== LISTING TRAINEES ===");
        gymFacade.listAllTrainees().forEach(t -> System.out.println("Trainee: " + t));

        // CREATE A TRAINING
        if (!gymFacade.listAllTrainers().isEmpty() && !gymFacade.listAllTrainees().isEmpty()) {
            Trainer firstTrainer = gymFacade.listAllTrainers().get(0);
            Trainee firstTrainee = gymFacade.listAllTrainees().get(0);

            Training training = new Training(
                    firstTrainer,
                    firstTrainee,
                    "Morning Yoga",
                    TrainingType.YOGA,
                    LocalDate.now(),
                    60
            );
            gymFacade.addTraining(training);
            System.out.println("\nCreated Training: " + training);
        }
    }
}
