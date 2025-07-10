package com.yourorg.routedashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    
    // Explicit constructor
    public DataLoader() {
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CarQueryAPI integration enabled - using real vehicle data from API");
        System.out.println("No local vehicle data needed - fetching from CarQueryAPI on demand");
    }
} 