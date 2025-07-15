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

    // Explicit constructor
    public DataLoader() {
    }

    @Override
    public void run(String... args) throws Exception {
        if (vehicleRepository.count() > 0) {
            System.out.println("Vehicle table already populated. Skipping seed.");
            return;
        }
        System.out.println("Seeding vehicle data from vehicles_seed.csv...");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("vehicles_seed.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            String[] line;
            int count = 0;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                Vehicle vehicle = Vehicle.builder()
                        .make(line[0])
                        .model(line[1])
                        .year(Integer.parseInt(line[2]))
                        .trim(line[3])
                        .fuelConsumptionCity(parseDouble(line[4]))
                        .fuelConsumptionHighway(parseDouble(line[5]))
                        .fuelConsumptionMixed(parseDouble(line[6]))
                        .build();
                vehicleRepository.save(vehicle);
                count++;
            }
            System.out.println("Seeded " + count + " vehicles.");
        } catch (Exception e) {
            System.err.println("Error seeding vehicle data: " + e.getMessage());
        }
    }

    private Double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return null; }
    }
} 