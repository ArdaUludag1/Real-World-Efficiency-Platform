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
    public List<String> getMakes(@RequestParam Integer year) {
        List<String> makes = vehicleService.getMakesByYear(year);
        System.out.println("Returned makes for year " + year + ": " + makes);
        return makes;
    }

    @GetMapping("/models")
    public List<String> getModels(@RequestParam Integer year, @RequestParam String make) {
        List<String> models = vehicleService.getModelsByYearAndMake(year, make);
        System.out.println("Returned models for year " + year + ", make " + make + ": " + models);
        return models;
    }

    @GetMapping("/trims")
    public List<String> getTrims(@RequestParam Integer year, @RequestParam String make, @RequestParam String model) {
        List<String> trims = vehicleService.getTrimsByMakeModelYear(make, model, year);
        System.out.println("Returned trims for year " + year + ", make " + make + ", model " + model + ": " + trims);
        return trims;
    }

    @GetMapping("/details")
    public Object getVehicleDetails(@RequestParam Integer year, @RequestParam String make, @RequestParam String model, @RequestParam String trim) {
        var details = vehicleService.getVehicleDetails(make, model, trim, year);
        System.out.println("Returned details for year " + year + ", make " + make + ", model " + model + ", trim " + trim + ": " + details);
        return details;
    }
} 