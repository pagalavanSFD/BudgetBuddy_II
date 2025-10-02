package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.Service.RecurringDepositService;
import com.example.budgetbuddy.model.RecurringDeposit;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.RecurringDepositRepository;
import com.example.budgetbuddy.repository.SavingsGoalRepository;
import com.example.budgetbuddy.repository.UserRepository;
import com.example.budgetbuddy.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recurring-deposit")
@CrossOrigin(origins = "http://localhost:3000")
public class RecurringDepositController {

    private final RecurringDepositRepository depositRepository;
    private final UserRepository userRepository;
    private final SavingsGoalRepository goalRepository;
    private final JwtUtil jwtUtil;
    private final RecurringDepositService recurringDepositService;

    public RecurringDepositController(RecurringDepositRepository depositRepository,
                                      UserRepository userRepository,
                                      SavingsGoalRepository goalRepository,
                                      JwtUtil jwtUtil,
                                      RecurringDepositService recurringDepositService) {
        this.depositRepository = depositRepository;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.jwtUtil = jwtUtil;
        this.recurringDepositService = recurringDepositService;
    }

    // Helper method to get authenticated user from JWT
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get all recurring deposits for the authenticated user
    @GetMapping
    public ResponseEntity<List<RecurringDeposit>> getUserDeposits(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        List<RecurringDeposit> deposits = depositRepository.findByUser(user);
        return ResponseEntity.ok(deposits);
    }

    // Create a new recurring deposit
    @PostMapping
    public ResponseEntity<RecurringDeposit> createDeposit(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RecurringDeposit deposit
    ) {
        User user = getUserFromToken(authHeader);
        deposit.setUser(user);
        deposit.setNextDepositDate(LocalDate.now());
        RecurringDeposit savedDeposit = depositRepository.save(deposit);
        return ResponseEntity.ok(savedDeposit);
    }

    // Delete a recurring deposit (with ownership check)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeposit(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        User user = getUserFromToken(authHeader);
        RecurringDeposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deposit not found"));

        if (!deposit.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        depositRepository.delete(deposit);
        return ResponseEntity.noContent().build();
    }

    // Process due recurring deposits immediately
    @PostMapping("/process")
    public ResponseEntity<String> processNow() {
        recurringDepositService.processDeposits();
        return ResponseEntity.ok("Recurring deposits processed successfully!");
    }
}
