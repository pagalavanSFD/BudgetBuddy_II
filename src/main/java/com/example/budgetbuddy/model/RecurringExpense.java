package com.example.budgetbuddy.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;

    private BigDecimal amount; // use BigDecimal for money

    private String frequency; // "WEEKLY" or "MONTHLY"
    private LocalDate nextExpenseDate;

    @ManyToOne
    private User user;
}
