package com.yourorg.routedashboard.service;

import com.yourorg.routedashboard.entity.History;
import com.yourorg.routedashboard.entity.User;
import com.yourorg.routedashboard.repository.HistoryRepository;
import com.yourorg.routedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    
    // Explicit constructor
    public HistoryService(HistoryRepository historyRepository, UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getUserHistory() {
        // In real app, get current user from security context
        // For demo, use first user
        User user = userRepository.findAll().stream().findFirst().orElse(null);
        
        if (user == null) {
            return List.of();
        }
        
        List<History> history = historyRepository.findByUserOrderByCreatedAtDesc(
            user, PageRequest.of(0, 10));
        
        return history.stream()
            .map(this::convertToMap)
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> convertToMap(History history) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", history.getId());
        map.put("make", history.getVehicle().getMake());
        map.put("model", history.getVehicle().getModel());
        map.put("year", history.getVehicle().getYear());
        map.put("fromCity", history.getFromCity());
        map.put("toCity", history.getToCity());
        map.put("distance", history.getDistanceKm());
        map.put("baseConsumption", history.getBaseConsumptionL());
        map.put("adjustedConsumption", history.getAdjustedConsumptionL());
        map.put("weatherFrom", history.getWeatherFrom());
        map.put("weatherTo", history.getWeatherTo());
        map.put("createdAt", history.getCreatedAt());
        return map;
    }
} 