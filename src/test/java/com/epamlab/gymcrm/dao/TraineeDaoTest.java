package com.epamlab.gymcrm.dao;

import com.epamlab.gymcrm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {
    private TraineeDao traineeDao;
    private Map<Long, Trainee> storage;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        traineeDao = new TraineeDao(storage);
    }

    @Test
    void create_ShouldPersistTrainee() {
        Trainee trainee = new Trainee("John", "Doe", true, LocalDate.now(), "Address");
        traineeDao.create(trainee);
        assertTrue(storage.containsKey(trainee.getUserId()));
    }

    @Test
    void read_ShouldReturnTrainee() {
        Trainee trainee = new Trainee("John", "Doe", true, LocalDate.now(), "Address");
        storage.put(trainee.getUserId(), trainee);
        assertEquals(trainee, traineeDao.read(trainee.getUserId()));
    }

    @Test
    void delete_ShouldRemoveTrainee() {
        Trainee trainee = new Trainee("John", "Doe", true, LocalDate.now(), "Address");
        storage.put(trainee.getUserId(), trainee);
        traineeDao.delete(trainee.getUserId());
        assertFalse(storage.containsKey(trainee.getUserId()));
    }
}