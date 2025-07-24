package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;
    
    // Removed explicit constructor (was duplicate)

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getHistory() {
        List<Map<String, Object>> history = historyService.getUserHistory();
        return ResponseEntity.ok(history);
    }
} 