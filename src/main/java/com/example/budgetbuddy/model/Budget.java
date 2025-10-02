package com.example.budgetbuddy.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "budget")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 15, scale = 2) // Example: 999 trillion max with 2 decimals
    private BigDecimal monthlyLimit;

    private int month; // 1-12
    private int year;  // e.g. 2025

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
