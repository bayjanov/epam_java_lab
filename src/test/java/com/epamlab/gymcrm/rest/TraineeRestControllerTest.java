package com.epamlab.gymcrm.rest;

import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.trainee.rest.TraineeRestController;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

@WebMvcTest(TraineeRestController.class)
public class TraineeRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean
    private GymFacade gymFacade;
    private Trainee sampleTrainee;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        sampleTrainee = new Trainee("John", "Doe", true, LocalDate.of(1990, 1, 1), "Street 1");
        sampleTrainee.setUsername("john.doe");
        sampleTrainee.setPassword("pass123");
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void registerTrainee_shouldReturnCredentials() throws Exception {
        String body = mapper.writeValueAsString(sampleTrainee);

        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void login_shouldReturn200IfValid() throws Exception {
        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);

        mockMvc.perform(get("/api/trainees/login")
                        .param("username", "john.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk());
    }

    @Test
    void changeLogin_shouldChangePassword() throws Exception {
        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(sampleTrainee);

        mockMvc.perform(put("/api/trainees/change-login")
                        .param("username", "john.doe")
                        .param("oldPassword", "pass123")
                        .param("newPassword", "newpass"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed"));
    }

    @Test
    void updateProfile_shouldReturnUpdatedProfile() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "Updated");
        updates.put("lastName", "Last");
        updates.put("dateOfBirth", "1990-01-01");
        updates.put("address", "New Address");
        updates.put("isActive", true);

        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(sampleTrainee);

        mockMvc.perform(put("/api/trainees/profile")
                        .param("username", "john.doe")
                        .param("password", "pass123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Last"));
    }

    @Test
    void deleteTrainee_shouldReturn200() throws Exception {
        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(sampleTrainee);

        mockMvc.perform(delete("/api/trainees/profile")
                        .param("username", "john.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee profile deleted"));
    }

    @Test
    void patchActivate_shouldChangeStatus() throws Exception {
        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);

        mockMvc.perform(patch("/api/trainees/activate")
                        .param("username", "john.doe")
                        .param("password", "pass123")
                        .param("isActive", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee activation updated"));
    }

    @Test
    void getUnassignedTrainers_shouldReturnList() throws Exception {
        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);
        when(gymFacade.getAvailableTrainersForTrainee("john.doe")).thenReturn(List.of(
                new Trainer("T1", "L1", "Yoga", true)
        ));

        mockMvc.perform(get("/api/trainees/not-assigned-trainers")
                        .param("username", "john.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainerFirstName").value("T1"));
    }

    @Test
    void updateTrainerList_shouldReturnTrainers() throws Exception {
        Map<String, List<String>> body = new HashMap<>();
        body.put("trainersList", List.of("trainer1"));

        Trainer t = new Trainer("trainer1", "L1", "Strength", true);
        t.setUsername("trainer1");
        t.setId(1L);
        sampleTrainee.setTrainers(Set.of(t));

        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTrainerByUsername("trainer1")).thenReturn(t);
        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(sampleTrainee);

        mockMvc.perform(put("/api/trainees/trainers-list")
                        .param("username", "john.doe")
                        .param("password", "pass123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainersList[0].trainerUsername").value("trainer1"));
    }

    @Test
    void getTrainings_shouldReturnList() throws Exception {
        Training tr = new Training(
                new Trainer("Jane", "Doe", "Strength", true),
                sampleTrainee,
                "Morning Yoga",
                TrainingType.YOGA,
                LocalDate.of(2024, 4, 1),
                60
        );

        when(gymFacade.authenticateTrainee("john.doe", "pass123")).thenReturn(true);
        when(gymFacade.getTraineeTrainings("john.doe", null, null, null, null))
                .thenReturn(List.of(tr));

        mockMvc.perform(get("/api/trainees/trainings")
                        .param("username", "john.doe")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Yoga"));
    }
}
