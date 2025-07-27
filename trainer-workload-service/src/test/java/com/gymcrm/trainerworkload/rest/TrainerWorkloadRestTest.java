package com.gymcrm.trainerworkload.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;
import com.gymcrm.trainerworkload.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerWorkloadRest.class)
class TrainerWorkloadRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerWorkloadService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnMonthlyDuration() throws Exception {
        Mockito.when(service.getMonthlyDuration("trainer1", 2025, 7)).thenReturn(Optional.of(150));

        mockMvc.perform(get("/api/workload/trainer1/2025/7")
                        .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trainer1"))
                .andExpect(jsonPath("$.totalMinutes").value(150));
    }

    @Test
    void shouldReturn404IfNoData() throws Exception {
        Mockito.when(service.getMonthlyDuration("trainer1", 2025, 7)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/workload/trainer1/2025/7")
                        .header("Authorization", "Bearer testtoken"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No data for given trainer / period"));
    }

    @Test
    void shouldProcessWorkloadEvent() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername("trainer1");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(LocalDate.now());
        request.setDuration(60);
        request.setActionType(TrainerWorkloadRequest.ActionType.ADD);

        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Transaction-Id", "tx-123")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(service).processWorkload(any(TrainerWorkloadRequest.class), Mockito.eq("tx-123"));
    }

    @Test
    void shouldFallbackTransactionIdIfMissing() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername("trainer1");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(LocalDate.now());
        request.setDuration(60);
        request.setActionType(TrainerWorkloadRequest.ActionType.ADD);

        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(service).processWorkload(any(TrainerWorkloadRequest.class), Mockito.eq("UNKNOWN"));
    }
}
