package com.yourorg.routedashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Explicit constructor
    public VehicleService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<String> getAllMakes() {
        try {
            String url = "https://www.carqueryapi.com/api.php?cmd=getMakes";
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode makesNode = jsonNode.get("Makes");
                
                List<String> makes = new ArrayList<>();
                if (makesNode.isArray()) {
                    for (JsonNode make : makesNode) {
                        String makeName = make.get("make_display").asText();
                        if (makeName != null && !makeName.isEmpty()) {
                            makes.add(makeName);
                        }
                    }
                }
                return makes;
            }
        } catch (Exception e) {
            System.err.println("Error fetching makes from CarQueryAPI: " + e.getMessage());
        }
        
        // Fallback to sample data if API fails
        return List.of("Toyota", "Honda", "Ford", "BMW", "Mercedes", "Audi", "Volkswagen", "Nissan");
    }

    public List<Integer> getYearsByMake(String make) {
        try {
            String url = String.format("https://www.carqueryapi.com/api.php?cmd=getYears&make=%s", 
                                     make.replace(" ", "%20"));
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode yearsNode = jsonNode.get("Years");
                
                Set<Integer> years = new TreeSet<>();
                if (yearsNode.isArray()) {
                    for (JsonNode year : yearsNode) {
                        try {
                            int yearValue = year.asInt();
                            // Range: 1994 to 2024 (30 years of vehicle data)
                            if (yearValue >= 1994 && yearValue <= 2024) {
                                years.add(yearValue);
                            }
                        } catch (Exception e) {
                            // Skip invalid year values
                        }
                    }
                }
                return new ArrayList<>(years);
            }
        } catch (Exception e) {
            System.err.println("Error fetching years from CarQueryAPI: " + e.getMessage());
        }
        
        // Fallback to all years from 1994 to 2024 if API fails
        List<Integer> fallbackYears = new ArrayList<>();
        for (int year = 1994; year <= 2024; year++) {
            fallbackYears.add(year);
        }
        return fallbackYears;
    }

    public List<String> getModelsByMakeAndYear(String make, Integer year) {
        try {
            String url = String.format("https://www.carqueryapi.com/api.php?cmd=getModels&make=%s&year=%d", 
                                     make.replace(" ", "%20"), year);
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode modelsNode = jsonNode.get("Models");
                
                List<String> models = new ArrayList<>();
                if (modelsNode.isArray()) {
                    for (JsonNode model : modelsNode) {
                        String modelName = model.get("model_name").asText();
                        if (modelName != null && !modelName.isEmpty()) {
                            models.add(modelName);
                        }
                    }
                }
                return models;
            }
        } catch (Exception e) {
            System.err.println("Error fetching models from CarQueryAPI: " + e.getMessage());
        }
        
        // Fallback to make-specific models if API fails
        return getFallbackModels(make);
    }
    
    private List<String> getFallbackModels(String make) {
        Map<String, List<String>> fallbackModels = new HashMap<>();
        
        // Toyota models
        fallbackModels.put("Toyota", List.of("Camry", "Corolla", "RAV4", "Highlander", "Tacoma", "Tundra", "Prius", "Sienna"));
        
        // Honda models
        fallbackModels.put("Honda", List.of("Civic", "Accord", "CR-V", "Pilot", "Odyssey", "HR-V", "Passport", "Ridgeline"));
        
        // Ford models
        fallbackModels.put("Ford", List.of("Focus", "Mustang", "F-150", "Explorer", "Escape", "Edge", "Expedition", "Ranger"));
        
        // BMW models
        fallbackModels.put("BMW", List.of("3 Series", "5 Series", "X3", "X5", "1 Series", "2 Series", "4 Series", "7 Series"));
        
        // Mercedes models
        fallbackModels.put("Mercedes", List.of("C-Class", "E-Class", "S-Class", "GLC", "GLE", "A-Class", "CLA", "GLA"));
        
        // Audi models
        fallbackModels.put("Audi", List.of("A4", "A6", "Q5", "Q7", "A3", "Q3", "A8", "TT"));
        
        // Volkswagen models
        fallbackModels.put("Volkswagen", List.of("Golf", "Passat", "Tiguan", "Atlas", "Jetta", "Arteon", "ID.4", "Taos"));
        
        // Nissan models
        fallbackModels.put("Nissan", List.of("Altima", "Maxima", "Rogue", "Murano", "Sentra", "Pathfinder", "Frontier", "Titan"));
        
        // Return make-specific models or generic list if make not found
        return fallbackModels.getOrDefault(make, List.of("Model 1", "Model 2", "Model 3"));
    }
} 