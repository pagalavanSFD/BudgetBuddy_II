package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.Budget;
import com.example.budgetbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserAndMonthAndYear(User user, int month, int year);
}
