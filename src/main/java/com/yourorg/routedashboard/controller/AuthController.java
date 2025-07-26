package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.config.JwtUtil;
import com.yourorg.routedashboard.dto.AuthResponse;
import com.yourorg.routedashboard.dto.LoginRequest;
import com.yourorg.routedashboard.dto.RegisterRequest;
import com.yourorg.routedashboard.entity.User;
import com.yourorg.routedashboard.repository.UserRepository;
import com.yourorg.routedashboard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            User user = userRepository.findByUsername(response.getUsername()).orElseThrow();
            String token = jwtUtil.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();
            response.setToken(token); // For testing; can be omitted in prod
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.authenticate(request);
            User user = userRepository.findByUsername(response.getUsername()).orElseThrow();
            String token = jwtUtil.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();
            response.setToken(token); // For testing; can be omitted in prod
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        System.out.println("Logout endpoint called");
        
        // Create a cookie that expires immediately to clear the JWT
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .secure(false) // Set to true in production with HTTPS
                .sameSite("Lax")
                .build();
        
        System.out.println("JWT cookie cleared");
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    // Global exception handler for validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Create a user-friendly error message
        Map<String, String> errorResponse = new HashMap<>();
        if (errors.containsKey("email")) {
            errorResponse.put("message", "Please enter a valid email address.");
        } else if (errors.containsKey("username")) {
            errorResponse.put("message", "Username must be between 3 and 50 characters.");
        } else if (errors.containsKey("password")) {
            errorResponse.put("message", "Password must be at least 6 characters long.");
        } else {
            errorResponse.put("message", "Please check your input and try again.");
        }
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
} 