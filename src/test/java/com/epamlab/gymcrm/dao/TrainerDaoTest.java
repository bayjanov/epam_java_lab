package com.epamlab.gymcrm.dao;

import com.epamlab.gymcrm.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoTest {
    private TrainerDao trainerDao;
    private Map<Long, Trainer> storage;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        trainerDao = new TrainerDao(storage);
    }

    @Test
    void create_ShouldPersistTrainer() {
        Trainer trainer = new Trainer("John", "Doe", "Yoga", true);
        trainerDao.create(trainer);
        assertTrue(storage.containsKey(trainer.getUserId()));
    }

    @Test
    void read_ShouldReturnTrainer() {
        Trainer trainer = new Trainer("John", "Doe", "Yoga", true);
        storage.put(trainer.getUserId(), trainer);
        assertEquals(trainer, trainerDao.read(trainer.getUserId()));
    }
}