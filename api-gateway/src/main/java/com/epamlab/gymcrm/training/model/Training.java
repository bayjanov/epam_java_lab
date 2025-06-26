package com.epamlab.gymcrm.training.model;

import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainer.model.Trainer;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trainingName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingType trainingType;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @Column(nullable = false)
    private int durationMinutes;

    // OneToMany?
    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    public Training() { }

    public Training(
            Trainer trainer, Trainee trainee,
            String trainingName, TrainingType trainingType,
            LocalDate trainingDate, int durationMinutes) {
        this.trainer = trainer;
        this.trainee = trainee;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.durationMinutes = durationMinutes;
    }

    // Getters/setters
    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public Trainee getTrainee() { return trainee; }
    public void setTrainee(Trainee trainee) { this.trainee = trainee; }

    public TrainingType getTrainingType() { return trainingType; }
    public void setTrainingType(TrainingType trainingType) { this.trainingType = trainingType; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

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
