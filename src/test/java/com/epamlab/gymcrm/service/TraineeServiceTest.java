package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TraineeDao;
import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.service.trainee.TraineeService;
import com.epamlab.gymcrm.service.user.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private TraineeDao traineeDao;

    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeService();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setUserProfileService(userProfileService);
    }

    @Test
    void createTrainee_ShouldGenerateCredentials() {
        // Mock the UserProfileService to return known values
        when(userProfileService.generateUniqueUsername(anyString(), anyString()))
                .thenReturn("alice.johnson");
        when(userProfileService.generatePassword(anyInt()))
                .thenReturn("password456");

        Trainee trainee = new Trainee("Alice", "Johnson", true, null, null);
        traineeService.createTrainee(trainee);

        assertNotNull(trainee.getUsername()); // Now "alice.johnson"
        assertNotNull(trainee.getPassword()); // Now "password456"
    }
}