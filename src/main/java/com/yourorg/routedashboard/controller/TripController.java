package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.entity.Trip;
import com.yourorg.routedashboard.entity.User;
import com.yourorg.routedashboard.entity.History;
import com.yourorg.routedashboard.repository.TripRepository;
import com.yourorg.routedashboard.repository.HistoryRepository;
import com.yourorg.routedashboard.service.VehicleService;
import com.yourorg.routedashboard.service.LocationWeatherService;
import com.yourorg.routedashboard.service.UserService;
import com.yourorg.routedashboard.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

@Controller
public class TripController {
    
    private final VehicleService vehicleService;
    private final LocationWeatherService locationWeatherService;
    private final TripRepository tripRepository;
    private final HistoryRepository historyRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    
    // Explicit constructor
    public TripController(VehicleService vehicleService, LocationWeatherService locationWeatherService, 
                        TripRepository tripRepository, HistoryRepository historyRepository, 
                        JwtUtil jwtUtil, UserService userService) {
        this.vehicleService = vehicleService;
        this.locationWeatherService = locationWeatherService;
        this.tripRepository = tripRepository;
        this.historyRepository = historyRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    
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
    
    @GetMapping("/trips")
    public String tripsPage(Model model, HttpServletRequest request) {
        // Get current user
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get dynamic makes and years from VehicleService
        List<String> makes = vehicleService.getAllMakes();
        List<Integer> years = vehicleService.getAllYears();
        
        model.addAttribute("makes", makes);
        model.addAttribute("years", years);
        model.addAttribute("locationWeatherEnabled", locationWeatherService.isGoogleMapsConfigured() && locationWeatherService.isOpenWeatherConfigured());
        model.addAttribute("username", currentUser.getUsername());
        
        return "trips";
    }
    
    @PostMapping("/trips/submit")
    public String submitTrip(@RequestParam String make,
                           @RequestParam String model,
                           @RequestParam String trim,
                           @RequestParam Integer year,
                           @RequestParam String fromCity,
                           @RequestParam String toCity,
                           @RequestParam Double distance,
                           @RequestParam Double fuelConsumption,
                           Model modelView,
                           HttpServletRequest request) {
        
        try {
            // Get current user
            User currentUser = getCurrentUser(request);
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            // Create a new Trip entity
            Trip trip = new Trip();
            trip.setUserId(currentUser.getId()); // Use current user's ID
            trip.setMake(make);
            trip.setModel(model);
            trip.setYear(year);
            trip.setFromCity(fromCity);
            trip.setToCity(toCity);
            trip.setDistanceKm(distance);
            trip.setFuelConsumptionActual(fuelConsumption);
            
            // Save the trip to the database
            tripRepository.save(trip);
            
            System.out.println("TripController: Trip saved successfully - ID: " + trip.getId());
            
            // Automatically save to history for backup/audit trail
            System.out.println("TripController: Creating history record for trip ID: " + trip.getId());
            
            try {
                History history = History.builder()
                        .tripId(trip.getId())
                        .user(currentUser)
                        .make(trip.getMake())
                        .model(trip.getModel())
                        .year(trip.getYear())
                        .fromCity(trip.getFromCity())
                        .toCity(trip.getToCity())
                        .distanceKm(trip.getDistanceKm())
                        .fuelConsumptionActual(trip.getFuelConsumptionActual())
                        .build();
                
                System.out.println("TripController: History object created successfully");
                historyRepository.save(history);
                System.out.println("TripController: History record saved successfully - ID: " + history.getId());
            } catch (Exception e) {
                System.err.println("TripController: Error saving history: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Redirect to dashboard to show the new trip in recent trips
            return "redirect:/dashboard?tripAdded=true";
            
        } catch (Exception e) {
            System.err.println("TripController: Error saving trip: " + e.getMessage());
            e.printStackTrace();
            // Redirect back to trips page with error
            return "redirect:/trips?error=true";
        }
    }

    @GetMapping("/api/weather")
    @ResponseBody
    public Map<String, Object> getWeatherData(@RequestParam String location) {
        System.out.println("TripController: Weather request for location: " + location);
        return locationWeatherService.getLocationAndWeather(location);
    }
    
    @GetMapping("/api/distance")
    @ResponseBody
    public Map<String, Object> calculateDistance(@RequestParam String origin, @RequestParam String destination) {
        System.out.println("TripController: Distance request from " + origin + " to " + destination);
        return locationWeatherService.calculateDistance(origin, destination);
    }
    
    @GetMapping("/api/trip")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTripDetails(@RequestParam Long id) {
        try {
            System.out.println("TripController: Fetching trip details for ID: " + id);
            
            // Find the trip by ID
            Trip trip = tripRepository.findById(id).orElse(null);
            
            if (trip == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Create response map with trip details
            Map<String, Object> tripDetails = Map.of(
                "id", trip.getId(),
                "year", trip.getYear(),
                "make", trip.getMake(),
                "model", trip.getModel(),
                "fromCity", trip.getFromCity(),
                "toCity", trip.getToCity(),
                "distanceKm", trip.getDistanceKm(),
                "fuelConsumption", trip.getFuelConsumptionActual(),
                "createdAt", trip.getCreatedAt().toString()
            );
            
            System.out.println("TripController: Trip details found: " + tripDetails);
            return ResponseEntity.ok(tripDetails);
            
        } catch (Exception e) {
            System.err.println("TripController: Error fetching trip details: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/api/trip")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTrip(@RequestParam Long id) {
        try {
            System.out.println("TripController: Deleting trip with ID: " + id);
            
            // Check if trip exists
            Trip trip = tripRepository.findById(id).orElse(null);
            
            if (trip == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Delete the trip
            tripRepository.deleteById(id);
            
            System.out.println("TripController: Trip deleted successfully - ID: " + id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Trip deleted successfully"
            ));
            
        } catch (Exception e) {
            System.err.println("TripController: Error deleting trip: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error deleting trip: " + e.getMessage()
            ));
        }
    }
}

@RestController
@RequestMapping("/api/trips")
class TripApiController {
    
    private final LocationWeatherService locationWeatherService;
    
    public TripApiController(LocationWeatherService locationWeatherService) {
        this.locationWeatherService = locationWeatherService;
    }
    
    @GetMapping("/location-weather")
    public ResponseEntity<Map<String, Object>> getLocationAndWeather(
            @RequestParam String location) {
        
        System.out.println("TripApiController: Received request for location: " + location);
        
        Map<String, Object> result = locationWeatherService.getLocationAndWeather(location);
        
        System.out.println("TripApiController: Service returned result: " + result);
        
        if (result.containsKey("error")) {
            System.out.println("TripApiController: Returning error response");
            return ResponseEntity.badRequest().body(result);
        }
        
        System.out.println("TripApiController: Returning success response");
        return ResponseEntity.ok(result);
    }
} 