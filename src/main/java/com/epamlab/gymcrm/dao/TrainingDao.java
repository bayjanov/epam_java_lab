package com.epamlab.gymcrm.dao;

import com.epamlab.gymcrm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class TrainingDao {
    private final Map<Long, Training> storage;

    @Autowired
    public TrainingDao(@Qualifier("trainingStorage") Map<Long, Training> storage) {
        this.storage = storage;
    }

    public void create(Training training) {
        storage.put(training.getId(), training);
    }

    public Training read(Long id) {
        return storage.get(id);
    }

    public void update(Training training) {
        storage.put(training.getId(), training);
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    public Map<Long, Training> listAll() {
        return storage;
    }
}