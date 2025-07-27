package com.gymcrm.trainerworkload.repository;

import com.gymcrm.trainerworkload.model.TrainerWorkload;
import com.gymcrm.trainerworkload.model.WorkloadMonth;
import com.gymcrm.trainerworkload.model.WorkloadYear;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class TrainerWorkloadRepositoryTest {

    @Autowired
    private TrainerWorkloadRepository repository;

    @Test
    void shouldFindByUsername() {
        TrainerWorkload trainer = new TrainerWorkload("user1", "John", "Doe", true);
        trainer.getYears().add(new WorkloadYear(2025, List.of(new WorkloadMonth(7, 120))));
        repository.save(trainer);

        var found = repository.findByUsername("user1");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldFindByFirstAndLastName() {
        TrainerWorkload trainer = new TrainerWorkload("user2", "Jane", "Smith", false);
        repository.save(trainer);

        var found = repository.findByFirstNameAndLastName("Jane", "Smith");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getUsername()).isEqualTo("user2");
    }
}
