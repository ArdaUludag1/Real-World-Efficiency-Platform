package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import com.yourorg.routedashboard.entity.Vehicle;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;
    
    // Explicit constructor
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/makes")
    public Object getMakes(@RequestParam Integer year) {
        try {
            List<String> makes = vehicleService.getMakesByYear(year);
            if (makes == null || makes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    java.util.Map.of("error", "CarQueryAPI returned no makes and no backup data available.")
                );
            }
            return makes;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                java.util.Map.of("error", "CarQueryAPI error: " + e.getMessage())
            );
        }
    }

    @GetMapping("/models")
    public Object getModels(@RequestParam Integer year, @RequestParam String make) {
        try {
            List<String> models = vehicleService.getModelsByYearAndMake(year, make);
            if (models == null || models.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    java.util.Map.of("error", "CarQueryAPI returned no models and no backup data available.")
                );
            }
            return models;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                java.util.Map.of("error", "CarQueryAPI error: " + e.getMessage())
            );
        }
    }

    @GetMapping("/trims")
    public Object getTrims(@RequestParam Integer year, @RequestParam String make, @RequestParam String model) {
        try {
            List<String> trims = vehicleService.getTrimsByMakeModelYear(make, model, year);
            if (trims == null || trims.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    java.util.Map.of("error", "CarQueryAPI returned no trims and no backup data available.")
                );
            }
            return trims;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                java.util.Map.of("error", "CarQueryAPI error: " + e.getMessage())
            );
        }
    }

    @GetMapping("/details")
    public Object getVehicleDetails(@RequestParam Integer year, @RequestParam String make, @RequestParam String model, @RequestParam String trim) {
        var details = vehicleService.getVehicleDetails(make, model, trim, year);
        if (details.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(details);
        }
        return details;
    }

    @GetMapping("/cars")
    public String getAllCars(Model model) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "cars";
    }
} 