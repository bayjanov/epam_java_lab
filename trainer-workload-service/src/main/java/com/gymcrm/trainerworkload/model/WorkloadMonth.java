package com.gymcrm.trainerworkload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadMonth {
    private int month;
    private int totalDuration;
}
