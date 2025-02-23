package com.epamlab.gymcrm.dao;

import com.epamlab.gymcrm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class TraineeDao {
    private final Map<Long, Trainee> storage;

    @Autowired
    public TraineeDao(@Qualifier("traineeStorage") Map<Long, Trainee> storage) {
        this.storage = storage;
    }

    public void create(Trainee trainee) {
        storage.put(trainee.getUserId(), trainee);
    }

    public Trainee read(Long id) {
        return storage.get(id);
    }

    public void update(Trainee trainee) {
        storage.put(trainee.getUserId(), trainee);
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    public Map<Long, Trainee> listAll() {
        return storage;
    }
}