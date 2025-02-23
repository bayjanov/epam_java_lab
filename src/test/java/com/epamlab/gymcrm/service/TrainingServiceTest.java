package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TrainingDao;
import com.epamlab.gymcrm.model.Training;
import com.epamlab.gymcrm.service.training.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private Map<Long, Training> trainingStorage;

    @InjectMocks
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void createTraining_ShouldPersist() {
        trainingDao = new TrainingDao(trainingStorage);
        trainingService.setTrainingDao(trainingDao);

        Training training = new Training();
        trainingService.createTraining(training);

        assertTrue(training.getId() > 0);
    }
}