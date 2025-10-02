package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.Service.UserService;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.security.JwtUtil;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService service;
    private final JwtUtil jwtUtil;

    public UserController(UserService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return service.registerUser(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        Optional<User> existing = service.loginUser(user.getEmail(), user.getPassword());
        if (existing.isPresent()) {
            String token = jwtUtil.generateToken(existing.get().getEmail()); // FIXED
            return Map.of("token", token);
        }
        return Map.of("error", "Invalid credentials");
    }

}
