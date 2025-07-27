package com.gymcrm.trainerworkload.messaging;

import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;
import com.gymcrm.trainerworkload.service.TrainerWorkloadService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


@Component
@Validated
@RequiredArgsConstructor
public class TrainerWorkloadConsumer {

    private final TrainerWorkloadService trainerWorkloadService;

    @JmsListener(destination = "trainer.workload.queue")
    public void receiveWorkload(@Valid TrainerWorkloadRequest request, Message message) throws JMSException {
        if (request.getUsername() == null || request.getTrainingDate() == null) {
            throw new IllegalArgumentException("Missing required fields in TrainerWorkloadRequest");
        }

        // Extract transactionId from message headers (if present)
        String transactionId = message.getStringProperty("transactionId");
        if (transactionId == null) {
            transactionId = "UNKNOWN"; // fallback
        }

        trainerWorkloadService.processWorkload(request, transactionId);
    }
}
