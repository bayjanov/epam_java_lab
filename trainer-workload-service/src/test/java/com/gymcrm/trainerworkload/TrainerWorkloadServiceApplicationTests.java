package com.gymcrm.trainerworkload;

import com.gymcrm.trainerworkload.service.TrainerWorkloadService;
import com.gymcrm.trainerworkload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TrainerWorkloadServiceApplicationTests {

	@Autowired
	private TrainerWorkloadService service;

	@Autowired
	private TrainerWorkloadRepository repository;

	@Test
	void contextLoads() {
		assertThat(service).isNotNull();
		assertThat(repository).isNotNull();
	}
}
