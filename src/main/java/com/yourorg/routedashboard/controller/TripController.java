package com.yourorg.routedashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TripController {
    
    @GetMapping("/trips")
    public String tripsPage(Model model) {
        model.addAttribute("makes", List.of("Toyota", "Honda", "Ford", "BMW", "Mercedes", "Audi", "Volkswagen", "Nissan", "Chevrolet", "Hyundai", "Kia", "Lexus", "Mazda", "Mitsubishi", "Porsche", "Subaru", "Tesla", "Volvo"));
        
        List<Integer> years = new ArrayList<>();
        for (int y = 1994; y <= 2024; y++) {
            years.add(y);
        }
        model.addAttribute("years", years);
        
        return "trips";
    }
    
    @PostMapping("/trips/submit")
    public String submitTrip(@RequestParam String make,
                           @RequestParam String model,
                           @RequestParam Integer year,
                           @RequestParam String fromCity,
                           @RequestParam String toCity,
                           @RequestParam Double distance,
                           @RequestParam Double fuelConsumption,
                           Model modelView) {
        
        // For now, just redirect back to trips page
        // In a real app, you would save this to the database
        return "redirect:/trips?success=true";
    }
} 