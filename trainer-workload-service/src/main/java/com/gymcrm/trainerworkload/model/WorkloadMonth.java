package com.gymcrm.trainerworkload.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`month`")
    private int month;

    private int totalDuration;

    public WorkloadMonth(int month, int totalDuration) {
        this.month = month;
        this.totalDuration = totalDuration;
    }
}

