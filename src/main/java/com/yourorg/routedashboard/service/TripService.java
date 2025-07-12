package com.yourorg.routedashboard.service;

import com.yourorg.routedashboard.dto.TripRequest;
import com.yourorg.routedashboard.entity.Trip;
import com.yourorg.routedashboard.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${google.api.key}")
    private String googleApiKey;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Create a new trip
    public Trip createTrip(TripRequest request, Long userId) {
        try {
            // Calculate distance using Google Maps Distance Matrix API
            Double distanceKm = calculateDistance(request.getFromCity(), request.getToCity());
            
            // Create and save the trip
            Trip trip = new Trip(
                userId,
                request.getMake(),
                request.getModel(),
                request.getYear(),
                request.getFromCity(),
                request.getToCity(),
                distanceKm,
                request.getFuelConsumptionActual()
            );
            
            return tripRepository.save(trip);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create trip: " + e.getMessage(), e);
        }
    }
    
    // Calculate distance between two cities using Google Maps API
    private Double calculateDistance(String fromCity, String toCity) {
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s&units=metric",
                fromCity.replace(" ", "+"),
                toCity.replace(" ", "+"),
                googleApiKey
            );
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            if (root != null && root.has("rows") && root.get("rows").size() > 0) {
                JsonNode row = root.get("rows").get(0);
                if (row.has("elements") && row.get("elements").size() > 0) {
                    JsonNode element = row.get("elements").get(0);
                    if (element.has("distance") && element.get("distance").has("value")) {
                        // Convert meters to kilometers
                        return element.get("distance").get("value").asDouble() / 1000.0;
                    }
                }
            }
            
            // Fallback: estimate distance (rough calculation)
            return estimateDistance(fromCity, toCity);
            
        } catch (Exception e) {
            // Fallback: estimate distance
            return estimateDistance(fromCity, toCity);
        }
    }
    
    // Fallback distance estimation (very rough)
    private Double estimateDistance(String fromCity, String toCity) {
        // This is a very basic fallback - in a real app you'd use a more sophisticated approach
        // For now, return a reasonable default distance
        return 50.0; // Default 50km
    }
    
    // Get all trips for a user
    public List<Trip> getUserTrips(Long userId) {
        return tripRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // Get the most recent trip for a user
    public Optional<Trip> getMostRecentTrip(Long userId) {
        return tripRepository.findMostRecentByUserId(userId);
    }
    
    // Get trip by ID
    public Optional<Trip> getTripById(Long tripId) {
        return tripRepository.findById(tripId);
    }
    
    // Delete trip
    public void deleteTrip(Long tripId) {
        tripRepository.deleteById(tripId);
    }
    
    // Get trip count for user
    public long getTripCount(Long userId) {
        return tripRepository.countByUserId(userId);
    }
} 