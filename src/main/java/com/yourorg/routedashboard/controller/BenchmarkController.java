package com.yourorg.routedashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BenchmarkController {
    
    @GetMapping("/benchmark")
    public String benchmarkPage(Model model) {
        // Sample benchmark data
        List<String> categories = List.of("Compact", "Sedan", "SUV", "Luxury", "Electric", "Hybrid");
        model.addAttribute("categories", categories);
        model.addAttribute("averageEfficiency", 8.5);
        model.addAttribute("userEfficiency", 7.2);
        model.addAttribute("benchmarkData", new ArrayList<>());
        
        return "benchmark";
    }
} 