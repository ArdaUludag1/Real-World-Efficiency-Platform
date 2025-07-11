package com.yourorg.routedashboard.service;

import com.yourorg.routedashboard.dto.RouteRequest;
import com.yourorg.routedashboard.dto.RouteResponse;
import com.yourorg.routedashboard.entity.History;
import com.yourorg.routedashboard.entity.User;
import com.yourorg.routedashboard.entity.Vehicle;
import com.yourorg.routedashboard.repository.HistoryRepository;
import com.yourorg.routedashboard.repository.UserRepository;
import com.yourorg.routedashboard.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final VehicleRepository vehicleRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final WeatherService weatherService;
    private final RestTemplate restTemplate;
    
    @Value("${google.api.key}")
    private String googleApiKey;
    
    // Explicit constructor
    public RouteService(VehicleRepository vehicleRepository, HistoryRepository historyRepository, 
                       UserRepository userRepository, WeatherService weatherService, RestTemplate restTemplate) {
        this.vehicleRepository = vehicleRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.weatherService = weatherService;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public RouteResponse calculateRoute(RouteRequest request) {
        // Create a vehicle object from the request data
        // Since we're using CarQueryAPI, we don't need to find it in our database
        Vehicle vehicle = Vehicle.builder()
            .make(request.getMake())
            .model(request.getModel())
            .year(request.getYear())
            .createdAt(LocalDateTime.now())
            .build();
        
        // Save the vehicle to the database first
        vehicle = vehicleRepository.save(vehicle);
        
        // Calculate distance (simplified - in real app, use Google Maps API)
        double distance = calculateDistance(request.getFromCity(), request.getToCity());
        
        // Get real weather data from OpenWeatherMap API
        String weatherFrom = weatherService.getWeatherData(request.getFromCity());
        String weatherTo = weatherService.getWeatherData(request.getToCity());
        
        // Calculate fuel consumption
        double baseConsumption = (distance / 100.0) * 7.0; // 7 L/100 km
        double adjustedConsumption = baseConsumption;
        
        // Adjust for weather conditions using real weather data
        double weatherAdjustmentFactor = Math.max(
            weatherService.getWeatherAdjustmentFactor(weatherFrom),
            weatherService.getWeatherAdjustmentFactor(weatherTo)
        );
        
        adjustedConsumption = baseConsumption * weatherAdjustmentFactor;
        
        double difference = adjustedConsumption - baseConsumption;
        double differencePercentage = (difference / baseConsumption) * 100.0;
        
        // Create response
        RouteResponse response = new RouteResponse(
            request.getMake(), request.getModel(), request.getYear(),
            request.getFromCity(), request.getToCity(),
            distance, baseConsumption, adjustedConsumption,
            difference, differencePercentage, weatherFrom, weatherTo
        );
        
        // Save to history (simplified - in real app, get current user from security context)
        saveHistory(request, vehicle, response);
        
        return response;
    }
    
    private double calculateDistance(String fromCity, String toCity) {
        // Simplified distance calculation
        // In real app, use Google Maps Distance Matrix API
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                fromCity, toCity, googleApiKey
            );
            
            // For demo purposes, return a mock distance
            return 500.0; // Mock 500 km
        } catch (Exception e) {
            // Fallback to mock distance
            return 500.0;
        }
    }
    
    private void saveHistory(RouteRequest request, Vehicle vehicle, RouteResponse response) {
        // In real app, get current user from security context
        // For demo, use first user or create a mock user
        User user = userRepository.findAll().stream().findFirst().orElse(null);
        
        if (user != null) {
            History history = History.builder()
                .user(user)
                .vehicle(vehicle)
                .fromCity(request.getFromCity())
                .toCity(request.getToCity())
                .distanceKm(response.getDistance())
                .baseConsumptionL(response.getBaseConsumption())
                .adjustedConsumptionL(response.getAdjustedConsumption())
                .weatherFrom(response.getWeatherFrom())
                .weatherTo(response.getWeatherTo())
                .build();
            
            historyRepository.save(history);
        }
    }
} 