package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.RecurringDeposit;
import com.example.budgetbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringDepositRepository extends JpaRepository<RecurringDeposit, Long> {

    List<RecurringDeposit> findByUser(User user);

    // Find deposits that are due today or earlier
    List<RecurringDeposit> findByNextDepositDateBeforeOrNextDepositDateEquals(LocalDate date1, LocalDate date2);
}
