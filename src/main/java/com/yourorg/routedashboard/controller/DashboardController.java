package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.entity.Trip;
import com.yourorg.routedashboard.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    
    @Autowired
    private TripRepository tripRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // For now, we'll use a default user ID of 1
        // In a real application, this would come from the authenticated user
        Long userId = 1L;
        
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
        
        return "dashboard";
    }
} 