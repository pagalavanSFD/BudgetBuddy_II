package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.Service.BudgetService;
import com.example.budgetbuddy.model.Budget;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.UserRepository;
import com.example.budgetbuddy.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "http://localhost:3000")
public class BudgetController {

    private final BudgetService budgetService;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public BudgetController(BudgetService budgetService, UserRepository userRepo, JwtUtil jwtUtil) {
        this.budgetService = budgetService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return userRepo.findByEmail(email).orElseThrow();
    }

    @PostMapping
    public Budget setBudget(@RequestHeader("Authorization") String authHeader,
                            @RequestParam int month,
                            @RequestParam int year,
                            @RequestParam BigDecimal amount) {   // 🔹 BigDecimal instead of double
        User user = getUserFromToken(authHeader);
        return budgetService.setBudget(user, amount, month, year);
    }

    @GetMapping("/check")
    public BudgetService.BudgetStatus checkBudget(@RequestHeader("Authorization") String authHeader,
                                                  @RequestParam int month,
                                                  @RequestParam int year) {
        User user = getUserFromToken(authHeader);
        return budgetService.checkBudget(user, month, year);
    }
}
