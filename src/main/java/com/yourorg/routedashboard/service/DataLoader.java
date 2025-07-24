package com.yourorg.routedashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.yourorg.routedashboard.entity.Vehicle;
import com.yourorg.routedashboard.repository.VehicleRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.InputStreamReader;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    @Autowired
    private VehicleRepository vehicleRepository;
    
    // Removed explicit constructor (was duplicate)

    @Override
    public void run(String... args) throws Exception {
        // Vehicle seeding disabled for production. No action taken.
    }

    private Double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return null; }
    }
} 