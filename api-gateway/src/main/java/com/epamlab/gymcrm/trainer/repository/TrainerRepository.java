package com.epamlab.gymcrm.trainer.repository;

import com.epamlab.gymcrm.trainer.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    List<Trainer> findBySpecialization(String specialization);

    @Query("""
    SELECT t FROM Trainer t
    WHERE t.id NOT IN (
        SELECT tr2.id FROM Trainee t1
        JOIN t1.trainers tr2
        WHERE t1.id = :traineeId
    )
""")
    List<Trainer> findUnassignedTrainersForTrainee(@Param("traineeId") Long traineeId);
}
