package com.example.budgetbuddy.Service;

import com.example.budgetbuddy.model.RecurringDeposit;
import com.example.budgetbuddy.repository.RecurringDepositRepository;
import com.example.budgetbuddy.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecurringDepositService {

    @Autowired
    private RecurringDepositRepository recurringDepositRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    // Process recurring deposits daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void processDeposits() {
        LocalDate today = LocalDate.now();
        List<RecurringDeposit> dueDeposits = recurringDepositRepository
                .findByNextDepositDateBeforeOrNextDepositDateEquals(today, today);

        for (RecurringDeposit deposit : dueDeposits) {
            var goal = deposit.getGoal();

            if (goal != null && deposit.getAmount() != null) {
                BigDecimal current = goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;
                BigDecimal newAmount = current.add(deposit.getAmount());
                goal.setCurrentAmount(newAmount);
                savingsGoalRepository.save(goal);
            }

            // Update nextDepositDate
            if ("WEEKLY".equalsIgnoreCase(deposit.getFrequency())) {
                deposit.setNextDepositDate(deposit.getNextDepositDate().plusWeeks(1));
            } else if ("MONTHLY".equalsIgnoreCase(deposit.getFrequency())) {
                deposit.setNextDepositDate(deposit.getNextDepositDate().plusMonths(1));
            }
            recurringDepositRepository.save(deposit);
        }
    }
}
