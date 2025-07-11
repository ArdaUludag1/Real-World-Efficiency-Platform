package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.dto.RouteRequest;
import com.yourorg.routedashboard.dto.RouteResponse;
import com.yourorg.routedashboard.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;
    
    // Explicit constructor
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<?> calculateRoute(@Valid @RequestBody RouteRequest request) {
        try {
            RouteResponse response = routeService.calculateRoute(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Route calculation error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Route calculation failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 