package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.Income;
import com.example.budgetbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);
}
