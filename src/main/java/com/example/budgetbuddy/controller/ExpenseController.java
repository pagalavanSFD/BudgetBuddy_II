package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.Service.ExpenseService;
import com.example.budgetbuddy.Service.IncomeService;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.Income;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.UserRepository;
import com.example.budgetbuddy.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepo, JwtUtil jwtUtil, IncomeService incomeService) {
        this.expenseService = expenseService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.incomeService = incomeService;
    }

    // GET all expenses for logged-in user
    @GetMapping
    public List<Expense> getExpenses(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        return expenseService.getExpensesByUser(user);
    }

    // POST a new expense
    @PostMapping
    public Expense addExpense(@RequestHeader("Authorization") String authHeader,
                              @RequestBody Expense expense) {
        User user = getUserFromToken(authHeader);
        expense.setUser(user);

        if (expense.getDate() == null) {
            expense.setDate(LocalDateTime.now());
        }

        return expenseService.addExpense(expense);
    }

    // PUT update an existing expense
    @PutMapping("/{id}")
    public Expense updateExpense(@RequestHeader("Authorization") String authHeader,
                                 @PathVariable Long id,
                                 @RequestBody Expense updatedExpense) {
        User user = getUserFromToken(authHeader);

        Expense existingExpense = expenseService.getExpenseById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        if (!existingExpense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to edit this expense");
        }

        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());

        if (updatedExpense.getDate() != null) {
            existingExpense.setDate(updatedExpense.getDate());
        }

        return expenseService.addExpense(existingExpense);
    }

    // Monthly Summary
    @GetMapping("/summary/monthly")
    public Map<String, BigDecimal> getMonthlySummary(@RequestHeader("Authorization") String authHeader,
                                                     @RequestParam int year,
                                                     @RequestParam int month) {
        User user = getUserFromToken(authHeader);

        BigDecimal totalExpenses = expenseService.getExpensesByUser(user).stream()
                .filter(e -> e.getDate() != null &&
                        e.getDate().getYear() == year &&
                        e.getDate().getMonthValue() == month)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = incomeService.getIncomeByUser(user).stream()
                .filter(i -> i.getDate() != null &&
                        i.getDate().getYear() == year &&
                        i.getDate().getMonthValue() == month)
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> monthlySummary = new HashMap<>();
        monthlySummary.put("totalIncome", totalIncome);
        monthlySummary.put("totalExpenses", totalExpenses);
        monthlySummary.put("balance", totalIncome.subtract(totalExpenses));

        return monthlySummary;
    }

    // Category Summary
    @GetMapping("/summary/category")
    public Map<String, BigDecimal> getCategorySummary(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        List<Expense> expenses = expenseService.getExpensesByUser(user);

        Map<String, BigDecimal> categorySummary = new HashMap<>();
        for (Expense e : expenses) {
            categorySummary.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
        }
        return categorySummary;
    }

    // Overall Summary
    @GetMapping("/summary/overall")
    public Map<String, BigDecimal> getOverallSummary(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);

        BigDecimal totalExpenses = expenseService.getExpensesByUser(user).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = incomeService.getIncomeByUser(user).stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> overall = new HashMap<>();
        overall.put("totalIncome", totalIncome);
        overall.put("totalExpenses", totalExpenses);
        overall.put("balance", totalIncome.subtract(totalExpenses));

        return overall;
    }

    // DELETE an expense
    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "Expense deleted with id: " + id;
    }

    // Helper method
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
