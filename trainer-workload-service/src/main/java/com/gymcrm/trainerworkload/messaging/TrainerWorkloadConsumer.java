package com.gymcrm.trainerworkload.messaging;

import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;
import com.gymcrm.trainerworkload.service.TrainerWorkloadService;
import jakarta.jms.JMSException;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadConsumer {

    private final TrainerWorkloadService trainerWorkloadService;

    @JmsListener(destination = "trainer.workload.queue")
    public void receiveWorkload(TrainerWorkloadRequest request) throws JMSException {
        if (request.getUsername() == null || request.getTrainingDate() == null) {
            throw new IllegalArgumentException("Missing required fields in TrainerWorkloadRequest");
        }

        trainerWorkloadService.processWorkload(request);
    }
}
