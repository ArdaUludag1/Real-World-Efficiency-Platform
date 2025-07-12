package com.yourorg.routedashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SettingsController {
    
    @GetMapping("/settings")
    public String settingsPage(Model model) {
        model.addAttribute("username", "user@example.com");
        model.addAttribute("notifications", true);
        model.addAttribute("units", "metric");
        return "settings";
    }
    
    @PostMapping("/settings/update")
    public String updateSettings(@RequestParam String username,
                               @RequestParam(required = false) Boolean notifications,
                               @RequestParam String units,
                               Model model) {
        
        // For now, just redirect back to settings page
        // In a real app, you would save these settings to the database
        return "redirect:/settings?updated=true";
    }
} 