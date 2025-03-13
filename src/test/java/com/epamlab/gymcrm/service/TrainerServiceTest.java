package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.dao.TrainerDao;
import com.epamlab.gymcrm.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        // trainer
        trainer = new Trainer("Trainer", "One", "Cardio", true);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setSpecialization("Strength");
        trainer.setActive(true);
        // password - so that authentication does not fail unexpectedly.
        trainer.setPassword("password");
    }

    // Trainer profile
    @Test
    void testCreateTrainer_GeneratesCredentials() {
        when(userProfileService.generateUniqueUsername("John", "Doe"))
                .thenReturn("john.doe");
        when(userProfileService.generatePassword(10))
                .thenReturn("securePass123");

        trainerService.createTrainer(trainer);

        assertEquals("john.doe", trainer.getUsername());
        assertEquals("securePass123", trainer.getPassword());
        verify(trainerDao).save(trainer);
    }

    // username/password matching
    @Test
    void testAuthenticate_Success() {
        Trainer mockTrainer = new Trainer("Trainer", "One", "Cardio", true);
        mockTrainer.setPassword("correctPass");
        when(trainerDao.findByUsername("john.doe")).thenReturn(mockTrainer);

        assertTrue(trainerService.authenticateTrainer("john.doe", "correctPass"));
    }

    @Test
    void testAuthenticate_Failure() {
        when(trainerDao.findByUsername("john.doe")).thenReturn(trainer);
        // password set in SetUp already
        assertFalse(trainerService.authenticateTrainer("john.doe", "wrongPass"));
    }

    @Test
    void testGetTrainerByUsername() {
        when(trainerDao.findByUsername("john.doe")).thenReturn(trainer);
        Trainer result = trainerService.getTrainerByUsername("john.doe");
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testChangePassword_Authenticated() {
        when(trainerDao.findByUsername("john.doe")).thenReturn(trainer);
        trainerService.changeTrainerPassword("john.doe", "newPass123");
        assertEquals("newPass123", trainer.getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    void testUpdateTrainer_ValidFields() {
        when(trainerDao.findByUsername("john.doe")).thenReturn(trainer);

        Trainer updated = new Trainer("Trainer", "One", "Cardio", true);
        updated.setFirstName("UpdatedName");
        updated.setSpecialization("Cardio");

        // Use the password ("password") so authentication passes.
        trainerService.updateTrainer("john.doe", "password", updated);

        assertEquals("UpdatedName", trainer.getFirstName());
        assertEquals("Cardio", trainer.getSpecialization());
        verify(trainerDao).update(trainer);
    }

    @Test
    void testUpdateTrainer_MissingFields() {
        // expect validation to fail because firstName is blank.
        Trainer invalidTrainer = new Trainer("Trainer", "One", "Cardio", true);
        invalidTrainer.setFirstName(""); // Invalid: firstName missing

        when(trainerDao.findByUsername("john.doe")).thenReturn(trainer);

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.updateTrainer("john.doe", "password", invalidTrainer));
    }

    @Test
    void testToggleActivation() {
        when(trainerDao.findByUsername("john.doe")).thenReturn(trainer);

        // Activate when already active: should warn, but not change the state.
        trainerService.activateTrainer("john.doe", "password");
        assertTrue(trainer.isActive());

        // Deactivate
        trainerService.deactivateTrainer("john.doe", "password");
        assertFalse(trainer.isActive());

        verify(trainerDao, times(2)).update(trainer);
    }

    @Test
    void testCreateTrainer_Validation() {
        Trainer invalidTrainer = new Trainer("Trainer", "One", "Cardio", true);
        invalidTrainer.setFirstName(""); // Missing required field

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer(invalidTrainer));
    }

    @Test
    void testAuthentication_InvalidUser() {
        when(trainerDao.findByUsername("nonexistent")).thenReturn(null);
        assertFalse(trainerService.authenticateTrainer("nonexistent", "pass"));
    }
}
