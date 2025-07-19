package com.epamlab.gymcrm.messaging;

import com.epamlab.gymcrm.client.dto.TrainerWorkloadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadProducer {

    private final JmsTemplate jmsTemplate;

    @Autowired
    public TrainerWorkloadProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void send(TrainerWorkloadRequest request) {
        jmsTemplate.convertAndSend("trainer.workload.queue", request);
    }
}
