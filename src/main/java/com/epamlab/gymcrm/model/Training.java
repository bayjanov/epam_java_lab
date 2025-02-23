package com.epamlab.gymcrm.model;

import java.time.LocalDateTime;

public class Training {
    private static long idCounter = 1;

    private Long id;
    private String name;
    private TrainingType trainingType;
    private LocalDateTime trainingDate;
    private int durationMinutes;

    // OneToMany?
    Trainer trainer;
    Trainee trainee;

    public Training() {
        this.id = idCounter++;
    }

    public Training(Trainer trainer, Trainee trainee,
                    TrainingType trainingType, LocalDateTime trainingDate,
                    int durationMinutes) {
        this.trainer = trainer;
        this.trainee = trainee;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.durationMinutes = durationMinutes;
        this.id = idCounter++;
    }

    // Getters/setters
    public Long getId() { return id; }

    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public Trainee getTrainee() { return trainee; }
    public void setTrainee(Trainee trainee) { this.trainee = trainee; }

    public TrainingType getTrainingType() { return trainingType; }
    public void setTrainingType(TrainingType trainingType) { this.trainingType = trainingType; }

    public LocalDateTime getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDateTime trainingDate) { this.trainingDate = trainingDate; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", trainer=" + (trainer != null ? trainer.getUsername() : "null") +
                ", trainee=" + (trainee != null ? trainee.getUsername() : "null") +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
