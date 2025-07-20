package com.yourorg.routedashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.yourorg.routedashboard.service.VehicleService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

@Controller
public class WebController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/")
    public String rootRedirect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/test-home")
    public String testHomePage() {
        return "home-test";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/cars")
    public String carsPage(Model model) {
        model.addAttribute("years", vehicleService.getAllYears());
        model.addAttribute("selectedYear", "");
        return "cars";
    }
} 