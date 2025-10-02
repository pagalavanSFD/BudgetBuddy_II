package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.RecurringExpense;
import com.example.budgetbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    List<RecurringExpense> findByNextExpenseDateBeforeOrNextExpenseDateEquals(LocalDate before, LocalDate equals);
    List<RecurringExpense> findByUser(User user);
}
