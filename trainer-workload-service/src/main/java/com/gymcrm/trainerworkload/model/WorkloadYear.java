package com.gymcrm.trainerworkload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadYear {
    private int year;
    private List<WorkloadMonth> months = new ArrayList<>();

    public WorkloadYear(int year) {
        this.year = year;
        this.months = new ArrayList<>();
    }
}
