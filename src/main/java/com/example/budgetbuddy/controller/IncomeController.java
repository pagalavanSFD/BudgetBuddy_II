package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.Service.IncomeService;
import com.example.budgetbuddy.model.Income;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.UserRepository;
import com.example.budgetbuddy.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/income")
@CrossOrigin(origins = "http://localhost:3000")
public class IncomeController {

    private final IncomeService incomeService;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public IncomeController(IncomeService incomeService, UserRepository userRepo, JwtUtil jwtUtil) {
        this.incomeService = incomeService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Get all incomes for logged-in user
    @GetMapping
    public List<Income> getIncomes(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        return incomeService.getIncomeByUser(user);
    }

    // ✅ Add new income
    @PostMapping
    public Income addIncome(@RequestHeader("Authorization") String authHeader,
                            @RequestBody Income income) {
        User user = getUserFromToken(authHeader);
        income.setUser(user);

        // If date not provided, set current timestamp
        if (income.getDate() == null) {
            income.setDate(LocalDateTime.now());
        }

        return incomeService.addIncome(income);
    }

    // Helper method to extract user from JWT
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
    
 // ✅ Update Income
    @PutMapping("/{id}")
    public Income updateIncome(@RequestHeader("Authorization") String authHeader,
                               @PathVariable Long id,
                               @RequestBody Income updatedIncome) {
        User user = getUserFromToken(authHeader);
        Income income = incomeService.getIncomeById(id);

        if (!income.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to update this income");
        }

        income.setAmount(updatedIncome.getAmount());
        income.setSource(updatedIncome.getSource());
        income.setDate(updatedIncome.getDate());

        return incomeService.addIncome(income); // save updated income
    }


    // ✅ Delete Income
    @DeleteMapping("/{id}")
    public String deleteIncome(@RequestHeader("Authorization") String authHeader,
                               @PathVariable Long id) {
        User user = getUserFromToken(authHeader);
        Income income = incomeService.getIncomeById(id);

        if (!income.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to delete this income");
        }

        incomeService.deleteIncome(id);
        return "Income deleted successfully";
    }

}
