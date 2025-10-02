package com.example.budgetbuddy.Service;

import com.example.budgetbuddy.model.Budget;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.Income;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.BudgetRepository;
import com.example.budgetbuddy.repository.ExpenseRepository;
import com.example.budgetbuddy.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepo;
    private final ExpenseRepository expenseRepo;
    private final IncomeRepository incomeRepo;

    public BudgetService(BudgetRepository budgetRepo, ExpenseRepository expenseRepo, IncomeRepository incomeRepo) {
        this.budgetRepo = budgetRepo;
        this.expenseRepo = expenseRepo;
        this.incomeRepo = incomeRepo;
    }

    public Budget setBudget(User user, BigDecimal amount, int month, int year) {
        Budget budget = budgetRepo.findByUserAndMonthAndYear(user, month, year)
                .orElse(new Budget());
        budget.setUser(user);
        budget.setMonthlyLimit(amount);
        budget.setMonth(month);
        budget.setYear(year);
        return budgetRepo.save(budget);
    }

    public Budget getBudget(User user, int month, int year) {
        return budgetRepo.findByUserAndMonthAndYear(user, month, year)
                .orElseThrow(() -> new RuntimeException("Budget not set for this month"));
    }

    public BudgetStatus checkBudget(User user, int month, int year) {
        Budget budget = getBudget(user, month, year);

        // Total expenses for this month
        BigDecimal totalExpenses = expenseRepo.findByUser(user).stream()
                .filter(e -> e.getDate().getMonthValue() == month && e.getDate().getYear() == year)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total income for this month
        BigDecimal totalIncome = incomeRepo.findByUser(user).stream()
                .filter(i -> i.getDate().getMonthValue() == month && i.getDate().getYear() == year)
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        String status;
        String message;

        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            status = "exceeded";
            message = "You have exceeded your budget by " + balance.negate();
        } else if (balance.compareTo(budget.getMonthlyLimit().multiply(BigDecimal.valueOf(0.1))) < 0) {
            status = "almost";
            message = "Budget almost reached, remaining " + balance;
        } else {
            status = "ok";
            message = "Budget OK, remaining balance: " + balance;
        }

        return new BudgetStatus(status, balance, message);
    }

    // Response DTO
    public static class BudgetStatus {
        private String status;
        private BigDecimal remaining;
        private String message;

        public BudgetStatus(String status, BigDecimal remaining, String message) {
            this.status = status;
            this.remaining = remaining;
            this.message = message;
        }

        public String getStatus() { return status; }
        public BigDecimal getRemaining() { return remaining; }
        public String getMessage() { return message; }
    }
}
