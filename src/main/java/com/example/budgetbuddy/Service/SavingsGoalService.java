package com.example.budgetbuddy.Service;

import com.example.budgetbuddy.model.SavingsGoal;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.SavingsGoalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SavingsGoalService {

    private final SavingsGoalRepository repo;

    public SavingsGoalService(SavingsGoalRepository repo) {
        this.repo = repo;
    }

    // Get all goals for a user
    public List<SavingsGoal> getGoalsByUser(User user) {
        return repo.findByUser(user);
    }

    // Update goal details
    public SavingsGoal updateGoal(SavingsGoal goal) {
        return repo.save(goal);
    }

    // Delete goal
    public void deleteGoal(Long id) {
        repo.deleteById(id);
    }

    public SavingsGoal depositToGoal(SavingsGoal goal, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("Invalid amount");
        BigDecimal current = goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;
        goal.setCurrentAmount(current.add(amount));
        return repo.save(goal);
    }

    public SavingsGoal withdrawFromGoal(SavingsGoal goal, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("Invalid amount");
        BigDecimal current = goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;
        if (current.compareTo(amount) < 0) throw new RuntimeException("Insufficient funds");
        goal.setCurrentAmount(current.subtract(amount));
        return repo.save(goal);
    }


 // Recommended monthly savings to reach goal
 public double calculateMonthlySavings(SavingsGoal goal) {
     BigDecimal target = goal.getTargetAmount() != null ? goal.getTargetAmount() : BigDecimal.ZERO;
     BigDecimal current = goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;
     BigDecimal remaining = target.subtract(current);

     long monthsLeft = ChronoUnit.MONTHS.between(
             LocalDate.now().withDayOfMonth(1),
             goal.getDeadline().withDayOfMonth(1)
     );
     monthsLeft = monthsLeft > 0 ? monthsLeft : 1;

     return remaining.divide(BigDecimal.valueOf(monthsLeft), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
 }

 public SavingsGoal addGoal(SavingsGoal goal) {
     goal.setCurrentAmount(BigDecimal.ZERO); // Initialize saved amount correctly
     return repo.save(goal);
 }
}
