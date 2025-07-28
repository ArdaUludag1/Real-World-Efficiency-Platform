package com.yourorg.realworldefficiency.controller;

import com.yourorg.realworldefficiency.service.HistoryService;
import com.yourorg.realworldefficiency.entity.History;
import com.yourorg.realworldefficiency.entity.User;
import com.yourorg.realworldefficiency.repository.HistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final HistoryService historyService;
    private final HistoryRepository historyRepository;

    public HistoryController(HistoryService historyService, HistoryRepository historyRepository) {
        this.historyService = historyService;
        this.historyRepository = historyRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getHistory() {
        List<Map<String, Object>> history = historyService.getUserHistory();
        return ResponseEntity.ok(history);
    }
    
    // Demo endpoint to show all history records (for presentation)
    @GetMapping("/all")
    public ResponseEntity<List<History>> getAllHistory() {
        List<History> allHistory = historyRepository.findAll();
        return ResponseEntity.ok(allHistory);
    }
    
    // Demo endpoint to show history by user (for presentation)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<History>> getHistoryByUser(@PathVariable Long userId) {
        // For demo purposes, we'll just return all history
        // In real app, you'd get the actual user from database
        List<History> allHistory = historyRepository.findAll();
        return ResponseEntity.ok(allHistory);
    }
    
    // Test endpoint to create a sample history record
    @PostMapping("/test-create")
    public ResponseEntity<String> createTestHistory() {
        try {
            // Create a test history record
            History testHistory = History.builder()
                    .tripId(1L)
                    .user(new User()) // This will be set by the system
                    .make("Test Make")
                    .model("Test Model")
                    .year(2020)
                    .fromCity("Test From")
                    .toCity("Test To")
                    .distanceKm(100.0)
                    .fuelConsumptionActual(8.5)
                    .build();
            
            historyRepository.save(testHistory);
            return ResponseEntity.ok("Test history record created successfully - ID: " + testHistory.getId());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating test history: " + e.getMessage());
        }
    }
} 
