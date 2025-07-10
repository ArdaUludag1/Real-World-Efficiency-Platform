package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<String>> getMakes() {
        List<String> makes = vehicleService.getAllMakes();
        return ResponseEntity.ok(makes);
    }

    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getYears(@RequestParam String make) {
        List<Integer> years = vehicleService.getYearsByMake(make);
        return ResponseEntity.ok(years);
    }

    @GetMapping("/models")
    public ResponseEntity<List<String>> getModels(@RequestParam String make, @RequestParam Integer year) {
        List<String> models = vehicleService.getModelsByMakeAndYear(make, year);
        return ResponseEntity.ok(models);
    }
} 