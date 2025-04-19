package com.epamlab.gymcrm.service;

import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainer.repository.TrainerRepository;
import com.epamlab.gymcrm.trainer.service.TrainerService;
import com.epamlab.gymcrm.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userProfileService;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer("John", "Doe", "Strength", true);
        trainer.setUsername("john.doe");
        trainer.setPassword("password");
    }

    @Test
    void testCreateTrainer_GeneratesCredentials() {
        when(userProfileService.generateUniqueUsername("John", "Doe")).thenReturn("john.doe");
        when(userProfileService.generateRawPassword(10)).thenReturn("securePass123");
        when(userProfileService.encodePassword("securePass123")).thenReturn("encoded123");

        trainerService.createTrainer(trainer);

        assertEquals("john.doe", trainer.getUsername());
        assertEquals("encoded123", trainer.getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testAuthenticate_Success() {
        when(trainerRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainer));
        when(userProfileService.matchesRawPassword("password", "password")).thenReturn(true);

        assertTrue(trainerService.authenticateTrainer("john.doe", "password"));
    }

    @Test
    void testAuthenticate_Failure() {
        when(trainerRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainer));

        assertFalse(trainerService.authenticateTrainer("john.doe", "wrongPass"));
    }

    @Test
    void testGetTrainerByUsername() {
        when(trainerRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerByUsername("john.doe");
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testChangePassword_Authenticated() {
        when(trainerRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainer));
        when(userProfileService.encodePassword("newPass123")).thenReturn("newPass123");

        trainerService.changeTrainerPassword("john.doe", "newPass123");

        assertEquals("newPass123", trainer.getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testUpdateTrainer_ValidFields() {
        when(trainerRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainer));
        when(userProfileService.matchesRawPassword("password", "password")).thenReturn(true);

        Trainer updated = new Trainer("Updated", "Doe", "Cardio", true);

        trainerService.updateTrainer("john.doe", "password", updated);

        assertEquals("Updated", trainer.getFirstName());
        assertEquals("Doe", trainer.getLastName());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testUpdateTrainer_MissingFields() {
        Trainer invalidTrainer = new Trainer("", "Doe", "Cardio", true);
        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainer));
        when(userProfileService.matchesRawPassword("password", "password")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.updateTrainer("john.doe", "password", invalidTrainer));
    }

    @Test
    void testActivateDeactivateTrainer() {
        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(trainer));
        when(userProfileService.matchesRawPassword("password", "password")).thenReturn(true);

        trainerService.activateTrainer("john.doe", "password");
        assertTrue(trainer.isActive());

        trainerService.deactivateTrainer("john.doe", "password");
        assertFalse(trainer.isActive());

        verify(trainerRepository, times(2)).save(trainer);
    }

    @Test
    void testCreateTrainer_Validation() {
        Trainer invalidTrainer = new Trainer("", "Doe", "Cardio", true);
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer(invalidTrainer));
    }

    @Test
    void testAuthentication_InvalidUser() {
        when(trainerRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        assertFalse(trainerService.authenticateTrainer("nonexistent", "pass"));
    }
}
