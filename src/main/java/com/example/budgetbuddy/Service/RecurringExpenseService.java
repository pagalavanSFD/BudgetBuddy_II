package com.example.budgetbuddy.Service;

import com.example.budgetbuddy.model.RecurringExpense;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.repository.ExpenseRepository;
import com.example.budgetbuddy.repository.RecurringExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecurringExpenseService {

    @Autowired
    private RecurringExpenseRepository recurringExpenseRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // Process recurring expenses daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void processExpenses() {
        LocalDate today = LocalDate.now();
        List<RecurringExpense> dueExpenses = recurringExpenseRepository
                .findByNextExpenseDateBeforeOrNextExpenseDateEquals(today, today);

        for (RecurringExpense recurring : dueExpenses) {
            // Create actual Expense entry
        	if (recurring.getAmount() != null) {
        	    Expense expense = new Expense();
        	    expense.setUser(recurring.getUser());
        	    expense.setDescription(recurring.getTitle()); // recurring still has "title"
        	    expense.setCategory(recurring.getCategory());
        	    expense.setAmount(recurring.getAmount());
        	    expense.setDate(today.atStartOfDay()); // ✅ LocalDate → LocalDateTime
        	    expenseRepository.save(expense);
        	}


            // Update nextExpenseDate
            if ("WEEKLY".equalsIgnoreCase(recurring.getFrequency())) {
                recurring.setNextExpenseDate(recurring.getNextExpenseDate().plusWeeks(1));
            } else if ("MONTHLY".equalsIgnoreCase(recurring.getFrequency())) {
                recurring.setNextExpenseDate(recurring.getNextExpenseDate().plusMonths(1));
            }

            recurringExpenseRepository.save(recurring);
        }
    }
}
