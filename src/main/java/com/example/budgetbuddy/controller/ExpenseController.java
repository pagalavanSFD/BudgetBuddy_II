package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.Service.ExpenseService;
import com.example.budgetbuddy.Service.IncomeService;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.Income;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.UserRepository;
import com.example.budgetbuddy.security.JwtUtil;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

        // Assign current timestamp if date is missing
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

        // Check ownership
        if (!existingExpense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to edit this expense");
        }

        // Update fields safely
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());

        // Update date only if provided
        if (updatedExpense.getDate() != null) {
            existingExpense.setDate(updatedExpense.getDate());
        }

        return expenseService.addExpense(existingExpense);
    }
    
    @GetMapping("/summary/monthly")
    public Map<String, Double> getMonthlySummary(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new RuntimeException("Invalid token");
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userRepo.findByEmail(email).orElseThrow();

        List<Expense> expenses = expenseService.getExpensesByUser(user);

        Map<String, Double> monthlySummary = new TreeMap<>();
        for (Expense e : expenses) {
            String month = e.getDate().getMonth().toString() + "-" + e.getDate().getYear();
            monthlySummary.put(month, monthlySummary.getOrDefault(month, 0.0) + e.getAmount());
        }
        return monthlySummary;
    }
    
    @GetMapping("/summary/category")
    public Map<String, Double> getCategorySummary(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) throw new RuntimeException("Invalid token");
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userRepo.findByEmail(email).orElseThrow();

        List<Expense> expenses = expenseService.getExpensesByUser(user);

        Map<String, Double> categorySummary = new HashMap<>();
        for (Expense e : expenses) {
            categorySummary.put(e.getCategory(), categorySummary.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }
        return categorySummary;
    }
    
    @GetMapping("/summary/overall")
    public Map<String, Double> getOverallSummary(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);

        double totalExpenses = expenseService.getExpensesByUser(user)
                                             .stream().mapToDouble(Expense::getAmount).sum();

        double totalIncome = incomeService.getIncomeByUser(user)
                                          .stream().mapToDouble(Income::getAmount).sum();

        Map<String, Double> overall = new HashMap<>();
        overall.put("totalIncome", totalIncome);
        overall.put("totalExpenses", totalExpenses);
        overall.put("balance", totalIncome - totalExpenses);
        return overall;
    }

    // DELETE an expense
    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "Expense deleted with id: " + id;
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
}
