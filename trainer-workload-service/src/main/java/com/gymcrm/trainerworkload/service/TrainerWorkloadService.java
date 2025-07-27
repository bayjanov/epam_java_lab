package com.gymcrm.trainerworkload.service;

import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;

import java.util.Optional;

public interface TrainerWorkloadService {
//    void processWorkload(TrainerWorkloadRequest request);
    void processWorkload(TrainerWorkloadRequest request, String transactionId);
    Optional<Integer> getMonthlyDuration(String username, int year, int month);
}



