package com.gymcrm.trainerworkload.service;

import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;
import com.gymcrm.trainerworkload.model.TrainerWorkload;
import com.gymcrm.trainerworkload.model.WorkloadMonth;
import com.gymcrm.trainerworkload.model.WorkloadYear;
import com.gymcrm.trainerworkload.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository repository;

    @Override
    public void processWorkload(TrainerWorkloadRequest dto, String transactionId) {
        log.info("txId={} - Processing workload for trainer {}", transactionId, dto.getUsername());

        // (a) Try to extract Trainer's record
        TrainerWorkload trainer = repository.findByUsername(dto.getUsername())
                .orElseGet(() -> {
                    TrainerWorkload newTrainer = new TrainerWorkload(
                            dto.getUsername(),
                            dto.getFirstName(),
                            dto.getLastName(),
                            dto.isActive()
                    );
                    log.debug("txId={} - Created new trainer document for {}", transactionId, dto.getUsername());
                    return newTrainer;
                });

        LocalDate date = dto.getTrainingDate();
        int year = date.getYear();
        int month = date.getMonthValue();

        // (b) Find or create WorkloadYear
        WorkloadYear workloadYear = trainer.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    WorkloadYear newYear = new WorkloadYear(year);
                    trainer.getYears().add(newYear);
                    log.debug("txId={} - Created new year {} for trainer {}", transactionId, year, dto.getUsername());
                    return newYear;
                });

        // (c) Find or create WorkloadMonth
        WorkloadMonth workloadMonth = workloadYear.getMonths().stream()
                .filter(y -> y.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    WorkloadMonth newMonth = new WorkloadMonth(month, 0);
                    workloadYear.getMonths().add(newMonth);
                    log.debug("txId={} - Created new month {} for trainer {}", transactionId, month, dto.getUsername());
                    return newMonth;
                });

        // (d) Update duration
        int delta = dto.getActionType() == TrainerWorkloadRequest.ActionType.ADD
                ? dto.getDuration()
                : -dto.getDuration();
        workloadMonth.setTotalDuration(workloadMonth.getTotalDuration() + delta);

        // (e) Save document
        repository.save(trainer);

        log.info("txId={} - Updated trainer {} workload: year={}, month={}, totalDuration={}",
                transactionId, dto.getUsername(), year, month, workloadMonth.getTotalDuration());
    }

    @Override
    public Optional<Integer> getMonthlyDuration(String username, int year, int month) {
        return repository.findByUsername(username)
                .flatMap(tw -> tw.getYears().stream()
                        .filter(y -> y.getYear() == year)
                        .findFirst()
                        .flatMap(y -> y.getMonths().stream()
                                .filter(m -> m.getMonth() == month)
                                .findFirst()
                        )
                )
                .map(WorkloadMonth::getTotalDuration);
    }
}
