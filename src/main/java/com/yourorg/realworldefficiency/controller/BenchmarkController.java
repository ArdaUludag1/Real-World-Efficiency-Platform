package com.yourorg.realworldefficiency.controller;

import com.yourorg.realworldefficiency.entity.Trip;
import com.yourorg.realworldefficiency.entity.User;
import com.yourorg.realworldefficiency.repository.TripRepository;
import com.yourorg.realworldefficiency.service.VehicleService;
import com.yourorg.realworldefficiency.service.UserService;
import com.yourorg.realworldefficiency.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BenchmarkController {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private VehicleService vehicleService;
    
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
    
    @GetMapping("/benchmark")
    public String benchmarkPage(Model model, HttpServletRequest request) {
        // Get current user
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Long userId = currentUser.getId();
        
        // Get user's trips
        List<Trip> userTrips = tripRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        // Calculate user's average efficiency
        double userAverageEfficiency = userTrips.isEmpty() ? 0.0 : 
                userTrips.stream()
                        .mapToDouble(Trip::getFuelConsumptionActual)
                        .average()
                        .orElse(0.0);
        
        // Calculate total distance
        double totalDistance = userTrips.stream()
                .mapToDouble(Trip::getDistanceKm)
                .sum();
        
        // Get unique vehicles for comparison
        Set<String> uniqueVehicles = userTrips.stream()
                .map(trip -> trip.getYear() + " " + trip.getMake() + " " + trip.getModel())
                .collect(Collectors.toSet());
        
        model.addAttribute("userTrips", userTrips);
        model.addAttribute("userAverageEfficiency", Math.round(userAverageEfficiency * 10.0) / 10.0);
        model.addAttribute("totalDistance", Math.round(totalDistance * 10.0) / 10.0);
        model.addAttribute("totalTrips", userTrips.size());
        model.addAttribute("uniqueVehicles", uniqueVehicles);
        model.addAttribute("username", currentUser.getUsername());
        
        return "benchmark";
    }
    
    @GetMapping("/api/benchmark/trips")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getUserTrips(HttpServletRequest request) {
        try {
            // Get current user
            User currentUser = getCurrentUser(request);
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }
            
            Long userId = currentUser.getId();
            List<Trip> userTrips = tripRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            List<Map<String, Object>> tripsData = userTrips.stream()
                    .map(trip -> {
                        Map<String, Object> tripData = new HashMap<>();
                        tripData.put("id", trip.getId());
                        tripData.put("year", trip.getYear());
                        tripData.put("make", trip.getMake());
                        tripData.put("model", trip.getModel());
                        tripData.put("fromCity", trip.getFromCity());
                        tripData.put("toCity", trip.getToCity());
                        tripData.put("distanceKm", trip.getDistanceKm());
                        tripData.put("fuelConsumption", trip.getFuelConsumptionActual());
                        tripData.put("createdAt", trip.getCreatedAt().toString());
                        tripData.put("vehicleKey", trip.getYear() + " " + trip.getMake() + " " + trip.getModel());
                        return tripData;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(tripsData);
            
        } catch (Exception e) {
            System.err.println("BenchmarkController: Error fetching user trips: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/api/benchmark/factory-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFactoryData(@RequestParam String year, 
                                                             @RequestParam String make, 
                                                             @RequestParam String model) {
        try {
            System.out.println("BenchmarkController: Fetching factory data for " + year + " " + make + " " + model);
            
            // Get factory data from VehicleService
            Map<String, Object> factoryData = vehicleService.getVehicleFactoryData(year, make, model);
            
            if (factoryData.containsKey("error")) {
                return ResponseEntity.badRequest().body(factoryData);
            }
            
            return ResponseEntity.ok(factoryData);
            
        } catch (Exception e) {
            System.err.println("BenchmarkController: Error fetching factory data: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching factory data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/api/benchmark/compare")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> compareEfficiency(@RequestParam Long tripId) {
        try {
            System.out.println("BenchmarkController: Comparing efficiency for trip ID: " + tripId);
            
            // Get the trip
            Trip trip = tripRepository.findById(tripId).orElse(null);
            if (trip == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Get factory data
            Map<String, Object> factoryData = vehicleService.getVehicleFactoryData(
                    trip.getYear().toString(), 
                    trip.getMake(), 
                    trip.getModel()
            );
            
            if (factoryData.containsKey("error")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Could not fetch factory data for comparison");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Calculate comparison metrics
            double userEfficiency = trip.getFuelConsumptionActual();
            double factoryEfficiency = (Double) factoryData.get("fuel_efficiency");
            
            double difference = userEfficiency - factoryEfficiency;
            double percentageDifference = (difference / factoryEfficiency) * 100;
            
            String efficiencyStatus;
            String statusColor;
            
            if (percentageDifference <= -10) {
                efficiencyStatus = "Excellent - You're doing better than factory rating!";
                statusColor = "success";
            } else if (percentageDifference <= 0) {
                efficiencyStatus = "Good - You're meeting factory expectations";
                statusColor = "info";
            } else if (percentageDifference <= 20) {
                efficiencyStatus = "Fair - Slightly above factory rating";
                statusColor = "warning";
            } else {
                efficiencyStatus = "Needs Improvement - Significantly above factory rating";
                statusColor = "danger";
            }
            
            // Create comparison result
            Map<String, Object> comparison = new HashMap<>();
            comparison.put("tripId", trip.getId());
            comparison.put("userEfficiency", userEfficiency);
            comparison.put("factoryEfficiency", factoryEfficiency);
            comparison.put("difference", Math.round(difference * 10.0) / 10.0);
            comparison.put("percentageDifference", Math.round(percentageDifference * 10.0) / 10.0);
            comparison.put("efficiencyStatus", efficiencyStatus);
            comparison.put("statusColor", statusColor);
            
            Map<String, Object> tripDetails = new HashMap<>();
            tripDetails.put("year", trip.getYear());
            tripDetails.put("make", trip.getMake());
            tripDetails.put("model", trip.getModel());
            tripDetails.put("fromCity", trip.getFromCity());
            tripDetails.put("toCity", trip.getToCity());
            tripDetails.put("distanceKm", trip.getDistanceKm());
            tripDetails.put("createdAt", trip.getCreatedAt().toString());
            
            comparison.put("tripDetails", tripDetails);
            comparison.put("factoryData", factoryData);
            
            return ResponseEntity.ok(comparison);
            
        } catch (Exception e) {
            System.err.println("BenchmarkController: Error comparing efficiency: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error comparing efficiency: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
} 
