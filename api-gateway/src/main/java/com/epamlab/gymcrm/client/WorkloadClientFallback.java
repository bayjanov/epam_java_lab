package com.epamlab.gymcrm.client;

import com.epamlab.gymcrm.client.dto.TrainerWorkloadRequest;
import org.springframework.stereotype.Component;

@Component
public class WorkloadClientFallback implements WorkloadClient {

    @Override
    public void sendWorkload(TrainerWorkloadRequest request) {
        System.err.println("Fallback triggered – workload not sent");
    }
}
