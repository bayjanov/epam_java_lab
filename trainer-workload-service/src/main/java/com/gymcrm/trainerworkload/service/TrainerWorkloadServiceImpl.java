package com.gymcrm.trainerworkload.service;

import com.gymcrm.trainerworkload.dto.TrainerWorkloadRequest;
import com.gymcrm.trainerworkload.model.TrainerWorkload;
import com.gymcrm.trainerworkload.model.WorkloadMonth;
import com.gymcrm.trainerworkload.model.WorkloadYear;
import com.gymcrm.trainerworkload.repository.TrainerWorkloadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    @Autowired
    private TrainerWorkloadRepository repository;

    @Override
    @Transactional
    public void processWorkload(TrainerWorkloadRequest dto) {
        TrainerWorkload trainer = repository.findByUsername(dto.getUsername())
                .orElseGet(() -> {
                    TrainerWorkload newTrainer = new TrainerWorkload(
                            dto.getUsername(),
                            dto.getFirstName(),
                            dto.getLastName(),
                            dto.isActive()
                    );
                    return repository.save(newTrainer);
                });

        LocalDate date =  dto.getTrainingDate();
        int year = date.getYear();
        int month = date.getMonthValue();

        // Find or create WorkloadYear
        WorkloadYear workloadYear = trainer.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    WorkloadYear newYear =  new WorkloadYear(year);
                    trainer.getYears().add(newYear);
                    return newYear;
                });

        // Find or create  WorkloadMonth
        WorkloadMonth workloadMonth = workloadYear.getMonths().stream()
                .filter(y -> y.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    WorkloadMonth newMonth = new WorkloadMonth(month, 0);
                    workloadYear.getMonths().add(newMonth);
                    return newMonth;
                });

        // Update duration
        int delta = dto.getActionType() == TrainerWorkloadRequest.ActionType.ADD
                ? dto.getDuration()
                : -dto.getDuration();

        workloadMonth.setTotalDuration(workloadMonth.getTotalDuration() + delta);

        // Save
        repository.save(trainer);
    }

    @Override
    @Transactional
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
