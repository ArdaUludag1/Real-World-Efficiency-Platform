package com.yourorg.routedashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalTrips", 0);
        model.addAttribute("totalDistance", 0.0);
        model.addAttribute("averageEfficiency", 0.0);
        model.addAttribute("recentTrips", new ArrayList<>());
        return "dashboard";
    }
} 