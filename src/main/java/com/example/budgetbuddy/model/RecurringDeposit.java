package com.example.budgetbuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class RecurringDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private SavingsGoal goal;

    @Column(nullable = false)
    private BigDecimal amount;

    private String frequency; // "WEEKLY" or "MONTHLY"

    private LocalDate nextDepositDate;
}
