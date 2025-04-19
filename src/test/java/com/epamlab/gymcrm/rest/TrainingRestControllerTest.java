package com.epamlab.gymcrm.rest;

import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.metrics.MetricsService;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.training.rest.TrainingRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TrainingRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private GymFacade gymFacade;
    @MockBean private MetricsService metricsService;

    private ObjectMapper mapper;

    private Trainer trainer;
    private Trainee trainee;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        trainer = new Trainer("Alice", "Trainer", "YOGA", true);
        trainer.setUsername("alice.trainer");
        trainer.setPassword("pass123");
        trainer.setId(1L);

        trainee = new Trainee("Bob", "Trainee", true, LocalDate.of(1995, 3, 1), "Street");
        trainee.setUsername("bob.trainee");
        trainee.setPassword("traineePass");
        trainee.setId(2L);
    }

    @Test
    void addTraining_shouldReturn200() throws Exception {
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("traineeUsername", "bob.trainee");
        reqBody.put("trainerUsername", "alice.trainer");
        reqBody.put("trainingName", "Morning Stretch");
        reqBody.put("trainingType", "YOGA");
        reqBody.put("trainingDate", "2024-04-10");
        reqBody.put("trainingDuration", 45);

        when(gymFacade.authenticateTrainer("alice.trainer", "pass123")).thenReturn(true);
        when(gymFacade.getTrainerByUsername("alice.trainer")).thenReturn(trainer);
        when(gymFacade.getTraineeByUsername("bob.trainee")).thenReturn(trainee);

        mockMvc.perform(post("/api/trainings/add")
                        .param("trainerUsername", "alice.trainer")
                        .param("trainerPassword", "pass123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reqBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Training added successfully"));
    }

    @Test
    void addTraining_shouldReturn401IfAuthFails() throws Exception {
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("traineeUsername", "bob.trainee");
        reqBody.put("trainerUsername", "alice.trainer");
        reqBody.put("trainingName", "Stretch");
        reqBody.put("trainingDate", "2024-04-10");
        reqBody.put("trainingDuration", 45);

        when(gymFacade.authenticateTrainer("alice.trainer", "wrongpass")).thenReturn(false);

        mockMvc.perform(post("/api/trainings/add")
                        .param("trainerUsername", "alice.trainer")
                        .param("trainerPassword", "wrongpass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reqBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTrainingTypes_shouldReturnEnumList() throws Exception {
        mockMvc.perform(get("/api/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingType").value("YOGA"))
                .andExpect(jsonPath("$[1].trainingTypeId").value(1)); // STRENGTH
    }
}
