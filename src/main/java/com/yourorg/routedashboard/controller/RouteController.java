package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.dto.RouteRequest;
import com.yourorg.routedashboard.dto.RouteResponse;
import com.yourorg.routedashboard.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<RouteResponse> calculateRoute(@Valid @RequestBody RouteRequest request) {
        RouteResponse response = routeService.calculateRoute(request);
        return ResponseEntity.ok(response);
    }
} 