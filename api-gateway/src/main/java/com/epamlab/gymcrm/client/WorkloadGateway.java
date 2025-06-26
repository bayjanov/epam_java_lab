package com.epamlab.gymcrm.client;

import com.epamlab.gymcrm.client.dto.TrainerWorkloadRequest;
import com.epamlab.gymcrm.security.jwt.JwtTokenProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
@Service
public class WorkloadGateway {

    private final WorkloadClient client;

    public WorkloadGateway(WorkloadClient client, JwtTokenProvider tokens) {
        this.client  = client;
    }

    @CircuitBreaker(name = "workload", fallbackMethod = "fallbackSend")
    public void send(TrainerWorkloadRequest req) {
        client.sendWorkload(req);
    }

    @SuppressWarnings("unused")
    private void fallbackSend(TrainerWorkloadRequest req, Throwable ex) {
        client.sendWorkload(req);
    }
}
