package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.entity.Trip;
import com.yourorg.routedashboard.entity.User;
import com.yourorg.routedashboard.repository.TripRepository;
import com.yourorg.routedashboard.service.UserService;
import com.yourorg.routedashboard.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import java.util.List;

@Controller
public class DashboardController {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    // Helper method to get current user from JWT token
    private User getCurrentUser(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (jwtUtil.validateToken(token)) {
                        String username = jwtUtil.getUsernameFromToken(token);
                        return userService.getUserByUsername(username).orElse(null);
                    }
                }
            }
        }
        return null;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        // Get current user
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Long userId = currentUser.getId();
        
        // Fetch all trips for the user
        List<Trip> userTrips = tripRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        // Calculate statistics
        int totalTrips = userTrips.size();
        double totalDistance = userTrips.stream()
                .mapToDouble(Trip::getDistanceKm)
                .sum();
        double averageEfficiency = userTrips.isEmpty() ? 0.0 : 
                userTrips.stream()
                        .mapToDouble(Trip::getFuelConsumptionActual)
                        .average()
                        .orElse(0.0);
        
        // Get recent trips (last 5)
        List<Trip> recentTrips = userTrips.stream()
                .limit(5)
                .toList();
        
        // Add data to model
        model.addAttribute("totalTrips", totalTrips);
        model.addAttribute("totalDistance", Math.round(totalDistance * 10.0) / 10.0);
        model.addAttribute("averageEfficiency", Math.round(averageEfficiency * 10.0) / 10.0);
        model.addAttribute("recentTrips", recentTrips);
        model.addAttribute("username", currentUser.getUsername());
        
        return "dashboard";
    }
} 