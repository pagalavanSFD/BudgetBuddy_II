package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.SavingsGoal;
import com.example.budgetbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
}
