package com.example.budgetbuddy.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double amount;
    private String category;

    @Column(name = "date") // optional, just to match DB column name
    private LocalDateTime date; // new field

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
