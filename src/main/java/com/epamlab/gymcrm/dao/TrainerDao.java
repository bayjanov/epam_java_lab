package com.epamlab.gymcrm.dao;

import com.epamlab.gymcrm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class TrainerDao {
    private final Map<Long, Trainer> storage;

    @Autowired
    public TrainerDao(@Qualifier("trainerStorage") Map<Long, Trainer> storage) {
        this.storage = storage;
    }

    public void create(Trainer trainer) {
        storage.put(trainer.getUserId(), trainer);
    }

    public Trainer read(Long id) {
        return storage.get(id);
    }

    public void update(Trainer trainer) {
        storage.put(trainer.getUserId(), trainer);
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    public Map<Long, Trainer> listAll() {
        return storage;
    }
}