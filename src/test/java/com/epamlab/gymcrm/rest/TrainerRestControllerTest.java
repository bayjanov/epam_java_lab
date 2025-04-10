package com.epamlab.gymcrm.rest;

import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.metrics.MetricsService;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class TrainerRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private GymFacade gymFacade;
    @MockBean private MetricsService metricsService;

    private Trainer trainer;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        trainer = new Trainer("Jane", "Doe", "Strength", true);
        trainer.setUsername("jane.doe");
        trainer.setPassword("pass123");
        mapper = new ObjectMapper();
    }

    @Test
    void registerTrainer_shouldReturnCredentials() throws Exception {
        String body = mapper.writeValueAsString(trainer);

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("jane.doe"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void loginTrainer_shouldReturn200IfValid() throws Exception {
        when(gymFacade.authenticateTrainer("jane.doe", "pass123")).thenReturn(true);

        mockMvc.perform(get("/api/trainers/login")
                        .param("username", "jane.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk());
    }

    @Test
    void changeLogin_shouldUpdatePassword() throws Exception {
        when(gymFacade.authenticateTrainer("jane.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTrainerByUsername("jane.doe")).thenReturn(trainer);

        mockMvc.perform(put("/api/trainers/change-login")
                        .param("username", "jane.doe")
                        .param("oldPassword", "pass123")
                        .param("newPassword", "newPass"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed"));
    }

    @Test
    void getProfile_shouldReturnTrainerWithTrainees() throws Exception {
        Trainee tr = new Trainee("John", "Smith", true, LocalDate.of(1990, 1, 1), "Address");
        tr.setUsername("john.smith");
        trainer.setTrainees(Set.of(tr));

        when(gymFacade.authenticateTrainer("jane.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTrainerByUsername("jane.doe")).thenReturn(trainer);

        mockMvc.perform(get("/api/trainers/profile")
                        .param("username", "jane.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.traineesList[0].traineeFirstName").value("John"));
    }

    @Test
    void updateProfile_shouldReturnUpdatedInfo() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "Updated");
        updates.put("lastName", "Trainer");
        updates.put("isActive", false);

        when(gymFacade.authenticateTrainer("jane.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTrainerByUsername("jane.doe")).thenReturn(trainer);

        mockMvc.perform(put("/api/trainers/profile")
                        .param("username", "jane.doe")
                        .param("password", "pass123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void patchActivate_shouldUpdateIsActiveStatus() throws Exception {
        when(gymFacade.authenticateTrainer("jane.doe", "pass123")).thenReturn(true);

        mockMvc.perform(patch("/api/trainers/activate")
                        .param("username", "jane.doe")
                        .param("password", "pass123")
                        .param("isActive", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer activation updated"));
    }

    @Test
    void getTrainings_shouldReturnList() throws Exception {
        Trainee t = new Trainee("John", "Smith", true, LocalDate.of(1990, 1, 1), "Street");
        Training tr = new Training(trainer, t, "Stretching", TrainingType.YOGA, LocalDate.now(), 45);

        when(gymFacade.authenticateTrainer("jane.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTrainerTrainings("jane.doe", null, null, null)).thenReturn(List.of(tr));

        mockMvc.perform(get("/api/trainers/trainings")
                        .param("username", "jane.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Stretching"));
    }
}
