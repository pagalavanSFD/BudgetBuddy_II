package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.Service.SavingsGoalService;
import com.example.budgetbuddy.model.SavingsGoal;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.UserRepository;
import com.example.budgetbuddy.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/savings-goal")
@CrossOrigin(origins = "http://localhost:3000")
public class SavingsGoalController {

    private final SavingsGoalService service;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public SavingsGoalController(SavingsGoalService service, UserRepository userRepo, JwtUtil jwtUtil) {
        this.service = service;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Create new savings goal
    @PostMapping
    public SavingsGoal addGoal(@RequestHeader("Authorization") String authHeader,
                               @RequestBody SavingsGoal goal) {
        goal.setUser(getUserFromToken(authHeader));
        return service.addGoal(goal);
    }

    // Get all goals for user
    @GetMapping
    public List<SavingsGoal> getGoals(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        return service.getGoalsByUser(user);
    }

    // Update goal
    @PutMapping("/{id}")
    public SavingsGoal updateGoal(@RequestHeader("Authorization") String authHeader,
                                  @PathVariable Long id,
                                  @RequestBody SavingsGoal updatedGoal) {
        User user = getUserFromToken(authHeader);
        SavingsGoal goal = service.getGoalsByUser(user)
                                  .stream()
                                  .filter(g -> g.getId().equals(id))
                                  .findFirst()
                                  .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setGoalName(updatedGoal.getGoalName());
        goal.setTargetAmount(updatedGoal.getTargetAmount());
        goal.setDeadline(updatedGoal.getDeadline());

        return service.updateGoal(goal);
    }

    // Delete goal
    @DeleteMapping("/{id}")
    public String deleteGoal(@RequestHeader("Authorization") String authHeader,
                             @PathVariable Long id) {
        service.deleteGoal(id);
        return "Savings goal deleted successfully";
    }

 // Deposit money
    @PostMapping("/{id}/deposit")
    public SavingsGoal deposit(@RequestHeader("Authorization") String authHeader,
                               @PathVariable Long id,
                               @RequestParam BigDecimal amount) {   // <-- use BigDecimal
        User user = getUserFromToken(authHeader);
        SavingsGoal goal = service.getGoalsByUser(user)
                                  .stream()
                                  .filter(g -> g.getId().equals(id))
                                  .findFirst()
                                  .orElseThrow(() -> new RuntimeException("Goal not found"));

        return service.depositToGoal(goal, amount); // update service method to accept BigDecimal
    }

    // Withdraw money
    @PostMapping("/{id}/withdraw")
    public SavingsGoal withdraw(@RequestHeader("Authorization") String authHeader,
                                @PathVariable Long id,
                                @RequestParam BigDecimal amount) {  // <-- use BigDecimal
        User user = getUserFromToken(authHeader);
        SavingsGoal goal = service.getGoalsByUser(user)
                                  .stream()
                                  .filter(g -> g.getId().equals(id))
                                  .findFirst()
                                  .orElseThrow(() -> new RuntimeException("Goal not found"));

        return service.withdrawFromGoal(goal, amount); // update service method to accept BigDecimal
    }


    // Recommended monthly savings
    @GetMapping("/recommended/{id}")
    public double getRecommended(@RequestHeader("Authorization") String authHeader,
                                 @PathVariable Long id) {
        User user = getUserFromToken(authHeader);
        SavingsGoal goal = service.getGoalsByUser(user)
                                  .stream()
                                  .filter(g -> g.getId().equals(id))
                                  .findFirst()
                                  .orElseThrow(() -> new RuntimeException("Goal not found"));

        return service.calculateMonthlySavings(goal);
    }
}
