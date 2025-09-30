package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.date BETWEEN :start AND :end")
    List<Expense> findByUserAndDateBetween(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
