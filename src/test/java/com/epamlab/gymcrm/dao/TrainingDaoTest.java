package com.epamlab.gymcrm.dao;

import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoTest {
    private TrainingDao trainingDao;
    private Map<Long, Training> storage;

    @BeforeEach
    void setUp() {
        storage = new java.util.HashMap<>();
        trainingDao = new TrainingDao(storage);
    }

    @Test
    void create_ShouldPersistTrainingInStorage() {
        Training training = new Training();
        trainingDao.create(training);
        assertTrue(storage.containsKey(training.getId()));
    }

    @Test
    void read_ShouldReturnTrainingWhenExists() {
        Training training = new Training();
        trainingDao.create(training);
        Training retrieved = trainingDao.read(training.getId());
        assertEquals(training, retrieved);
    }

    @Test
    void read_ShouldReturnNullWhenNotExists() {
        Training retrieved = trainingDao.read(999L);
        assertNull(retrieved);
    }

    @Test
    void update_ShouldReplaceExistingTraining() {
        Training original = new Training();
        trainingDao.create(original);

        // Modify the training
        original.setDurationMinutes(60);
        trainingDao.update(original);

        Training updated = trainingDao.read(original.getId());
        assertEquals(60, updated.getDurationMinutes());
    }

    @Test
    void delete_ShouldRemoveTrainingFromStorage() {
        Training training = new Training();
        trainingDao.create(training);
        trainingDao.delete(training.getId());
        assertFalse(storage.containsKey(training.getId()));
    }

    @Test
    void listAll_ShouldReturnAllTrainings() {
        Training training1 = new Training();
        Training training2 = new Training();
        trainingDao.create(training1);
        trainingDao.create(training2);

        Map<Long, Training> allTrainings = trainingDao.listAll();
        assertEquals(2, allTrainings.size());
        assertTrue(allTrainings.containsValue(training1));
        assertTrue(allTrainings.containsValue(training2));
    }
}