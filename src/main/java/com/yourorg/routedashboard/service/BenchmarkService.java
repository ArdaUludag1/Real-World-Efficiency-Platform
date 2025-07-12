package com.yourorg.routedashboard.service;

import com.yourorg.routedashboard.dto.BenchmarkResult;
import com.yourorg.routedashboard.entity.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BenchmarkService {
    
    @Autowired
    private TripService tripService;
    
    // Get benchmark result for the most recent trip
    public Optional<BenchmarkResult> getBenchmarkResult(Long userId) {
        Optional<Trip> recentTrip = tripService.getMostRecentTrip(userId);
        
        if (recentTrip.isPresent()) {
            Trip trip = recentTrip.get();
            Double baselineConsumption = getBaselineConsumption(trip.getMake(), trip.getModel(), trip.getYear());
            
            BenchmarkResult result = new BenchmarkResult(
                trip.getMake(),
                trip.getModel(),
                trip.getYear(),
                trip.getFromCity(),
                trip.getToCity(),
                trip.getDistanceKm(),
                trip.getFuelConsumptionActual(),
                baselineConsumption
            );
            
            return Optional.of(result);
        }
        
        return Optional.empty();
    }
    
    // Get baseline consumption for a vehicle (based on make, model, year)
    private Double getBaselineConsumption(String make, String model, Integer year) {
        // This is a simplified baseline calculation
        // In a real application, you might:
        // 1. Use CarQueryAPI to get official fuel economy data
        // 2. Use EPA or other official sources
        // 3. Calculate based on vehicle class and year
        
        // For now, we'll use a simple baseline based on year and make
        Double baseConsumption = 7.0; // Default baseline
        
        // Adjust based on year (newer cars are generally more efficient)
        if (year >= 2020) {
            baseConsumption = 6.5;
        } else if (year >= 2015) {
            baseConsumption = 7.0;
        } else if (year >= 2010) {
            baseConsumption = 7.5;
        } else if (year >= 2000) {
            baseConsumption = 8.0;
        } else {
            baseConsumption = 9.0;
        }
        
        // Adjust based on make (some brands are known for efficiency)
        switch (make.toLowerCase()) {
            case "toyota":
            case "honda":
            case "hyundai":
            case "kia":
                baseConsumption -= 0.5; // More efficient
                break;
            case "bmw":
            case "mercedes":
            case "audi":
                baseConsumption += 0.5; // Less efficient (luxury)
                break;
            case "ford":
            case "chevrolet":
            case "dodge":
                baseConsumption += 0.3; // Slightly less efficient
                break;
        }
        
        // Ensure reasonable bounds
        return Math.max(4.0, Math.min(15.0, baseConsumption));
    }
    
    // Get benchmark result for a specific trip
    public Optional<BenchmarkResult> getBenchmarkResultForTrip(Long tripId) {
        Optional<Trip> trip = tripService.getTripById(tripId);
        
        if (trip.isPresent()) {
            Trip tripData = trip.get();
            Double baselineConsumption = getBaselineConsumption(tripData.getMake(), tripData.getModel(), tripData.getYear());
            
            BenchmarkResult result = new BenchmarkResult(
                tripData.getMake(),
                tripData.getModel(),
                tripData.getYear(),
                tripData.getFromCity(),
                tripData.getToCity(),
                tripData.getDistanceKm(),
                tripData.getFuelConsumptionActual(),
                baselineConsumption
            );
            
            return Optional.of(result);
        }
        
        return Optional.empty();
    }
} 