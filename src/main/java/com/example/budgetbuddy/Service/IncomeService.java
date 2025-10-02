package com.example.budgetbuddy.Service;

import com.example.budgetbuddy.model.Income;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.IncomeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class IncomeService {
    private final IncomeRepository repo;

    public IncomeService(IncomeRepository repo) {
        this.repo = repo;
    }

    public List<Income> getIncomeByUser(User user) {
        return repo.findByUser(user);
    }

    public Income addIncome(Income income) {
        if (income.getDate() == null) income.setDate(LocalDateTime.now());
        return repo.save(income);
    }
    
    public Income getIncomeById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));
    }

    public void deleteIncome(Long id) {
        repo.deleteById(id);
    }

}


