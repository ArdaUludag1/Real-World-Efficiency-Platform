package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.config.JwtUtil;
import com.yourorg.routedashboard.dto.AuthResponse;
import com.yourorg.routedashboard.dto.LoginRequest;
import com.yourorg.routedashboard.dto.RegisterRequest;
import com.yourorg.routedashboard.entity.User;
import com.yourorg.routedashboard.repository.UserRepository;
import com.yourorg.routedashboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    // Explicit constructor
    public AuthController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
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
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
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
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
} 