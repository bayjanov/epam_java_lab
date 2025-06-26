package com.epamlab.gymcrm.client;

import com.epamlab.gymcrm.client.dto.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trainer-workload-service",
        fallback = WorkloadClientFallback.class)
public interface WorkloadClient {

    @PostMapping("/api/workload")
    void sendWorkload(@RequestBody TrainerWorkloadRequest request);

}
