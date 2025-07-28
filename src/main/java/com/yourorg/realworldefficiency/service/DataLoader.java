package com.yourorg.realworldefficiency.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.yourorg.realworldefficiency.entity.Vehicle;
import com.yourorg.realworldefficiency.repository.VehicleRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.InputStreamReader;
import java.io.InputStream;
import com.yourorg.realworldefficiency.entity.Trip;
import com.yourorg.realworldefficiency.repository.TripRepository;
import com.yourorg.realworldefficiency.entity.User;
import com.yourorg.realworldefficiency.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Removed explicit constructor (was duplicate)

    @Override
    public void run(String... args) throws Exception {
        // Vehicle seeding disabled for production. No action taken.
    }

    private Double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return null; }
    }

    // Debug method to check trip data
    public void debugTripData() {
        System.out.println("=== DEBUG: Checking Trip Data ===");
        
        // Get all trips
        List<Trip> allTrips = tripRepository.findAll();
        System.out.println("Total trips in database: " + allTrips.size());
        
        // Group by user ID
        Map<Long, List<Trip>> tripsByUser = allTrips.stream()
                .collect(Collectors.groupingBy(Trip::getUserId));
        
        for (Map.Entry<Long, List<Trip>> entry : tripsByUser.entrySet()) {
            Long userId = entry.getKey();
            List<Trip> trips = entry.getValue();
            
            // Get user info
            Optional<User> userOpt = userRepository.findById(userId);
            String userInfo = userOpt.map(u -> u.getUsername() + " (" + u.getEmail() + ")")
                                   .orElse("User not found");
            
            System.out.println("User ID " + userId + " (" + userInfo + "): " + trips.size() + " trips");
            
            // Show trip details
            for (Trip trip : trips) {
                System.out.println("  - Trip ID: " + trip.getId() + 
                                 ", From: " + trip.getFromCity() + 
                                 " To: " + trip.getToCity() + 
                                 ", Distance: " + trip.getDistanceKm() + "km" +
                                 ", Created: " + trip.getCreatedAt());
            }
        }
        System.out.println("=== END DEBUG ===");
    }
} 
