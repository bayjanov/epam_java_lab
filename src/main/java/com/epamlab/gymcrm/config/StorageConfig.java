package com.epamlab.gymcrm.config;

import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StorageConfig {

    @Bean(name = "traineeStorage")
    public Map<Long, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean(name = "trainerStorage")
    public Map<Long, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean(name = "trainingStorage")
    public Map<Long, Training> trainingStorage() {
        return new HashMap<>();
    }
}