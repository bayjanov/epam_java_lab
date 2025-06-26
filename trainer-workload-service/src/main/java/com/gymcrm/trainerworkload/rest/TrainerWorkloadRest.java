package com.gymcrm.trainerworkload.rest;

import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;
import com.gymcrm.trainerworkload.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workload")
@RequiredArgsConstructor
public class TrainerWorkloadRest {
    private final TrainerWorkloadService service;


    @GetMapping("/{username}/{year}/{month}")
    public ResponseEntity<?> monthlyDuration(@PathVariable String username,
                                             @PathVariable int year,
                                             @PathVariable int month,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String bearer) {

        return service.getMonthlyDuration(username, year, month)
                .<ResponseEntity<?>>map(minutes -> ResponseEntity.ok(
                        Map.of("username", username,
                                "year", year,
                                "month", month,
                                "totalMinutes", minutes)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(Map.of("message", "No data for given trainer / period")));
    }

    @PostMapping
    public ResponseEntity<?> handleWorkloadEvent(
            @RequestBody TrainerWorkloadRequest request) {
        service.processWorkload(request);
        return ResponseEntity.ok().build();
    }
}
