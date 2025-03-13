package com.epamlab.gymcrm.training.repository;

import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findByTrainee_Username(String traineeUsername);
    List<Training> findByTrainer_Username(String trainerUsername);
    List<Training> findByTrainingType(TrainingType trainingType);
    List<Training> findByTrainingDate(LocalDate trainingDate);
    List<Training> findByDurationMinutes(int durationMinutes);

    @Query(value = """
    SELECT t.* 
    FROM trainings t
    JOIN trainees tr ON tr.id = t.trainee_id
    JOIN users u1 ON tr.id = u1.id
    JOIN trainers trn ON trn.id = t.trainer_id
    JOIN users u2 ON trn.id = u2.id
    WHERE u1.username = :traineeUsername
      AND (CAST(:from AS DATE) IS NULL OR t.training_date >= CAST(:from AS DATE))
      AND (CAST(:to AS DATE) IS NULL OR t.training_date <= CAST(:to AS DATE))
      AND (CAST(:trainerName AS VARCHAR) IS NULL OR (u2.first_name || ' ' || u2.last_name) ILIKE CONCAT('%', CAST(:trainerName AS VARCHAR), '%'))
      AND (CAST(:trainingType AS VARCHAR) IS NULL OR t.training_type = CAST(:trainingType AS VARCHAR))
    """, nativeQuery = true)
    List<Training> findTraineeTrainingsWithFilters(
            @Param("traineeUsername") String traineeUsername,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType
    );




    @Query("""
    SELECT t FROM Training t
    WHERE t.trainer.username = :trainerUsername
      AND t.trainingDate >= COALESCE(:fromDate, t.trainingDate)
      AND t.trainingDate <= COALESCE(:toDate, t.trainingDate)
      AND (
        :traineeName IS NULL 
        OR LOWER(t.trainee.lastName) LIKE LOWER(CONCAT('%', :traineeName, '%'))
      )
""")
    List<Training> findTrainerTrainingsWithFilters(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );

}
