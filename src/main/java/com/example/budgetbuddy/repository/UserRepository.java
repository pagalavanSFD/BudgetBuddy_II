package com.example.budgetbuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.budgetbuddy.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
