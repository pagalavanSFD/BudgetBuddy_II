package com.example.budgetbuddy.Service;

import org.springframework.stereotype.Service;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) {
        this.repo = repo;
    }

    public Expense addExpense(Expense expense) {
        if (expense.getDate() == null) {
            expense.setDate(LocalDateTime.now());
        }
        return repo.save(expense);
    }

    public List<Expense> getExpensesByUser(User user) {
        return repo.findByUser(user);
    }

    public void deleteExpense(Long id) {
        repo.deleteById(id);
    }

    public Optional<Expense> getExpenseById(Long id) {
        return repo.findById(id);
    }

    public Expense updateExpense(Expense existing, Expense updated) {
        existing.setDescription(updated.getDescription());
        existing.setAmount(updated.getAmount());  // BigDecimal
        existing.setCategory(updated.getCategory());
        if (updated.getDate() != null) {
            existing.setDate(updated.getDate());
        }
        return repo.save(existing);
    }

    public List<Expense> getMonthlyExpenses(User user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
        return repo.findByUserAndDateBetween(user, start, end);
    }

    // 🔹 Future utility method (total monthly expenses in BigDecimal)
    public BigDecimal getMonthlyExpenseTotal(User user, int year, int month) {
        return getMonthlyExpenses(user, year, month).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
