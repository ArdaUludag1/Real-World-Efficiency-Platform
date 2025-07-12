package com.yourorg.routedashboard.service;

import com.yourorg.routedashboard.dto.AuthResponse;
import com.yourorg.routedashboard.dto.LoginRequest;
import com.yourorg.routedashboard.dto.RegisterRequest;
import com.yourorg.routedashboard.entity.User;
import com.yourorg.routedashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Authentication methods
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return new AuthResponse(user.getUsername(), user.getEmail(), null);
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return new AuthResponse(user.getUsername(), user.getEmail(), null);
    }
    
    // User management methods
    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Save or update user
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    // Update user
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    // Check if username exists
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
} 