package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.model.RecurringExpense;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.RecurringExpenseRepository;
import com.example.budgetbuddy.repository.UserRepository;
import com.example.budgetbuddy.security.JwtUtil;
import com.example.budgetbuddy.Service.RecurringExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recurring-expense")
@CrossOrigin(origins = "http://localhost:3000")
public class RecurringExpenseController {

    @Autowired
    private RecurringExpenseRepository recurringExpenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RecurringExpenseService recurringExpenseService;

    // Get all recurring expenses for user
    @GetMapping
    public ResponseEntity<List<RecurringExpense>> getUserExpenses(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        List<RecurringExpense> expenses = recurringExpenseRepository.findByUser(user);
        return ResponseEntity.ok(expenses);
    }

    // Create a new recurring expense
    @PostMapping
    public ResponseEntity<RecurringExpense> createExpense(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RecurringExpense expense
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        expense.setUser(user);
        expense.setNextExpenseDate(LocalDate.now());

        RecurringExpense saved = recurringExpenseRepository.save(expense);
        return ResponseEntity.ok(saved);
    }

    // Delete a recurring expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable Long id) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        RecurringExpense expense = recurringExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        recurringExpenseRepository.delete(expense);
        return ResponseEntity.noContent().build();
    }

    // Manually process recurring expenses
    @PostMapping("/process")
    public ResponseEntity<String> processNow() {
        recurringExpenseService.processExpenses();
        return ResponseEntity.ok("Recurring expenses processed successfully!");
    }
}
