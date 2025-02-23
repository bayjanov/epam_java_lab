package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TrainerDao;
import com.epamlab.gymcrm.model.Trainer;
import com.epamlab.gymcrm.service.trainer.TrainerService;
import com.epamlab.gymcrm.service.user.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private TrainerDao trainerDao;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setUserProfileService(userProfileService);
    }

    @Test
    void createTrainer_ShouldGenerateCredentials() {
        // Mock the UserProfileService to return known values
        when(userProfileService.generateUniqueUsername(anyString(), anyString()))
                .thenReturn("test.user");
        when(userProfileService.generatePassword(anyInt()))
                .thenReturn("password123");

        Trainer trainer = new Trainer("Test", "User", "Yoga", true);
        trainerService.createTrainer(trainer);

        assertNotNull(trainer.getUsername()); // Now "test.user"
        assertNotNull(trainer.getPassword()); // Now "password123"
    }
}