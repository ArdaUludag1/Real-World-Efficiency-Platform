package com.yourorg.routedashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CarController {
    
    @GetMapping("/cars")
    public String carsPage(Model model, 
                          @RequestParam(required = false) String make,
                          @RequestParam(required = false) Integer year) {
        
        // Simple static data for now
        List<String> makes = List.of("Toyota", "Honda", "Ford", "BMW", "Mercedes", "Audi", "Volkswagen", "Nissan", "Chevrolet", "Hyundai", "Kia", "Lexus", "Mazda", "Mitsubishi", "Porsche", "Subaru", "Tesla", "Volvo");
        
        List<Integer> years = new ArrayList<>();
        for (int y = 1994; y <= 2024; y++) {
            years.add(y);
        }
        
        model.addAttribute("vehicles", new ArrayList<>());
        model.addAttribute("makes", makes);
        model.addAttribute("years", years);
        model.addAttribute("selectedMake", make != null ? make : "");
        model.addAttribute("selectedYear", year);
        
        return "cars";
    }
} 