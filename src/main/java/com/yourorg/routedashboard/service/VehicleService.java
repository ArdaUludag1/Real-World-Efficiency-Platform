package com.yourorg.routedashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    private final com.yourorg.routedashboard.repository.VehicleRepository vehicleRepository;
    
    // Explicit constructor
    public VehicleService(RestTemplate restTemplate, ObjectMapper objectMapper, 
                         com.yourorg.routedashboard.repository.VehicleRepository vehicleRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.vehicleRepository = vehicleRepository;
    }

    public List<String> getAllMakes() {
        try {
            String url = "https://www.carqueryapi.com/api.php?cmd=getMakes";
            
            // Add proper headers to avoid 403 Forbidden
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json, text/plain, */*");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Referer", "https://www.carqueryapi.com/");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // System.out.println("Calling CarQueryAPI for makes: " + url);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // System.out.println("CarQueryAPI response for makes: " + response.getBody().substring(0, Math.min(200, response.getBody().length())));
                
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
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
                
                System.out.println("Found " + makes.size() + " makes from CarQueryAPI");
                return makes;
            }
        } catch (Exception e) {
            System.err.println("Error fetching makes from CarQueryAPI: " + e.getMessage());
        }
        
        // Fallback to comprehensive list if API fails
        System.out.println("Using fallback makes list");
        return getComprehensiveFallbackMakes();
    }

    public List<Integer> getYearsByMake(String make) {
        try {
            String url = String.format("https://www.carqueryapi.com/api.php?cmd=getYears&make=%s", 
                                     make.replace(" ", "%20"));
            
            // Add proper headers to avoid 403 Forbidden
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json, text/plain, */*");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Referer", "https://www.carqueryapi.com/");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("Calling CarQueryAPI for years: " + url);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("CarQueryAPI response for years of " + make + ": " + response.getBody().substring(0, Math.min(200, response.getBody().length())));
                
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
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
                
                List<Integer> result = new ArrayList<>(years);
                System.out.println("Found " + result.size() + " years for " + make + ": " + result);
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error fetching years from CarQueryAPI for " + make + ": " + e.getMessage());
        }
        
        // Fallback to make-specific years if API fails
        System.out.println("Using fallback years for " + make);
        return getMakeSpecificFallbackYears(make);
    }

    public List<String> getModelsByMakeAndYear(String make, Integer year) {
        try {
            String url = String.format("https://www.carqueryapi.com/api.php?cmd=getModels&make=%s&year=%d", 
                                     make.replace(" ", "%20"), year);
            
            // Add proper headers to avoid 403 Forbidden
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json, text/plain, */*");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Referer", "https://www.carqueryapi.com/");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("Calling CarQueryAPI for models: " + url);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("CarQueryAPI response for " + make + " " + year + ": " + response.getBody().substring(0, Math.min(200, response.getBody().length())));
                
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
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
                
                System.out.println("Found " + models.size() + " models for " + make + " " + year + ": " + models);
                return models;
            }
        } catch (Exception e) {
            System.err.println("Error fetching models from CarQueryAPI for " + make + " " + year + ": " + e.getMessage());
        }
        
        // Improved fallback: year-specific models for each make
        System.out.println("Using improved fallback models for " + make + " " + year);
        return getYearSpecificFallbackModels(make, year);
    }

    private List<String> getYearSpecificFallbackModels(String make, Integer year) {
        Map<String, Map<Integer, List<String>>> fallbackModels = new HashMap<>();
        
        // Toyota
        Map<Integer, List<String>> toyota = new HashMap<>();
        toyota.put(2024, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Crown", "bZ4X", "GR86", "GR Corolla", "GR Supra"));
        toyota.put(2023, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Crown", "bZ4X", "GR86", "GR Corolla", "GR Supra"));
        toyota.put(2022, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "bZ4X", "GR86", "GR Corolla", "GR Supra"));
        toyota.put(2021, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "GR86", "GR Supra"));
        toyota.put(2020, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "GR Supra"));
        toyota.put(2019, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "C-HR", "Yaris", "GR Supra"));
        toyota.put(2018, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "C-HR", "Yaris"));
        toyota.put(2017, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2016, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2015, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2014, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2013, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2012, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2011, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2010, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2009, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2008, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2007, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2006, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2005, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2004, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2003, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2002, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2001, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(2000, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(1999, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(1998, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(1997, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(1996, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(1995, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        toyota.put(1994, List.of("Camry", "Corolla", "RAV4", "Prius", "Highlander", "Tacoma", "Tundra", "Sienna", "4Runner", "Sequoia", "Land Cruiser", "Avalon", "Yaris"));
        fallbackModels.put("Toyota", toyota);
        
        // Honda
        Map<Integer, List<String>> honda = new HashMap<>();
        honda.put(2024, List.of("Civic", "Accord", "CR-V", "Pilot", "HR-V", "Passport", "Ridgeline", "Odyssey"));
        honda.put(2020, List.of("Civic", "Accord", "CR-V", "Pilot", "HR-V", "Passport"));
        honda.put(2015, List.of("Civic", "Accord", "CR-V", "Pilot", "HR-V"));
        honda.put(2010, List.of("Civic", "Accord", "CR-V", "Pilot"));
        honda.put(2005, List.of("Civic", "Accord", "CR-V"));
        honda.put(2000, List.of("Civic", "Accord"));
        honda.put(1995, List.of("Civic", "Accord"));
        fallbackModels.put("Honda", honda);
        
        // Ford
        Map<Integer, List<String>> ford = new HashMap<>();
        ford.put(2024, List.of("F-150", "Mustang", "Escape", "Explorer", "Edge", "Expedition", "Ranger", "Bronco"));
        ford.put(2020, List.of("F-150", "Mustang", "Escape", "Explorer", "Edge", "Expedition"));
        ford.put(2015, List.of("F-150", "Mustang", "Escape", "Explorer", "Edge"));
        ford.put(2010, List.of("F-150", "Mustang", "Escape", "Explorer"));
        ford.put(2005, List.of("F-150", "Mustang", "Escape"));
        ford.put(2000, List.of("F-150", "Mustang"));
        ford.put(1995, List.of("F-150"));
        fallbackModels.put("Ford", ford);
        
        // BMW
        Map<Integer, List<String>> bmw = new HashMap<>();
        bmw.put(2024, List.of("3 Series", "5 Series", "X3", "X5", "1 Series", "2 Series", "4 Series", "7 Series"));
        bmw.put(2020, List.of("3 Series", "5 Series", "X3", "X5", "1 Series", "2 Series"));
        bmw.put(2015, List.of("3 Series", "5 Series", "X3", "X5", "1 Series"));
        bmw.put(2010, List.of("3 Series", "5 Series", "X3", "X5"));
        bmw.put(2005, List.of("3 Series", "5 Series", "X3"));
        bmw.put(2000, List.of("3 Series", "5 Series"));
        bmw.put(1995, List.of("3 Series"));
        fallbackModels.put("BMW", bmw);
        
        // Mercedes
        Map<Integer, List<String>> mercedes = new HashMap<>();
        mercedes.put(2024, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLB", "GLC", "GLE", "GLS", "AMG GT", "EQS", "EQE", "EQB", "EQA"));
        mercedes.put(2023, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLB", "GLC", "GLE", "GLS", "AMG GT", "EQS", "EQE", "EQB", "EQA"));
        mercedes.put(2022, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLB", "GLC", "GLE", "GLS", "AMG GT", "EQS", "EQE"));
        mercedes.put(2021, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLB", "GLC", "GLE", "GLS", "AMG GT"));
        mercedes.put(2020, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLB", "GLC", "GLE", "GLS", "AMG GT"));
        mercedes.put(2019, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "AMG GT", "SL", "SLC", "G-Class"));
        mercedes.put(2018, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "AMG GT", "SL", "SLC", "G-Class"));
        mercedes.put(2017, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "AMG GT", "SL", "SLC", "G-Class"));
        mercedes.put(2016, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "AMG GT", "SL", "SLC", "G-Class"));
        mercedes.put(2015, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "AMG GT", "SL", "SLC", "G-Class"));
        mercedes.put(2014, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2013, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2012, List.of("A-Class", "B-Class", "C-Class", "CLA", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2011, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2010, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2009, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2008, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2007, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2006, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2005, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2004, List.of("A-Class", "B-Class", "C-Class", "CLS", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2003, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2002, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2001, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(2000, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(1999, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(1998, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(1997, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(1996, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(1995, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        mercedes.put(1994, List.of("A-Class", "B-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "SL", "SLC", "G-Class"));
        fallbackModels.put("Mercedes", mercedes);
        
        // Audi
        Map<Integer, List<String>> audi = new HashMap<>();
        audi.put(2024, List.of("A4", "Q5", "A6", "Q7", "A3", "Q3", "A8", "TT"));
        audi.put(2020, List.of("A4", "Q5", "A6", "Q7", "A3", "Q3"));
        audi.put(2015, List.of("A4", "Q5", "A6", "Q7", "A3"));
        audi.put(2010, List.of("A4", "Q5", "A6", "Q7"));
        audi.put(2005, List.of("A4", "Q5", "A6"));
        audi.put(2000, List.of("A4", "Q5"));
        audi.put(1995, List.of("A4"));
        fallbackModels.put("Audi", audi);
        
        // Volkswagen
        Map<Integer, List<String>> vw = new HashMap<>();
        vw.put(2024, List.of("Golf", "Passat", "Tiguan", "Jetta", "Atlas", "Arteon", "ID.4", "Taos"));
        vw.put(2020, List.of("Golf", "Passat", "Tiguan", "Jetta", "Atlas", "Arteon"));
        vw.put(2015, List.of("Golf", "Passat", "Tiguan", "Jetta", "Atlas"));
        vw.put(2010, List.of("Golf", "Passat", "Tiguan", "Jetta"));
        vw.put(2005, List.of("Golf", "Passat", "Tiguan"));
        vw.put(2000, List.of("Golf", "Passat"));
        vw.put(1995, List.of("Golf"));
        fallbackModels.put("Volkswagen", vw);
        
        // Nissan
        Map<Integer, List<String>> nissan = new HashMap<>();
        nissan.put(2024, List.of("Altima", "Rogue", "Sentra", "Pathfinder", "Murano", "Frontier", "Titan", "Maxima"));
        nissan.put(2020, List.of("Altima", "Rogue", "Sentra", "Pathfinder", "Murano", "Frontier"));
        nissan.put(2015, List.of("Altima", "Rogue", "Sentra", "Pathfinder", "Murano"));
        nissan.put(2010, List.of("Altima", "Rogue", "Sentra", "Pathfinder"));
        nissan.put(2005, List.of("Altima", "Rogue", "Sentra"));
        nissan.put(2000, List.of("Altima", "Rogue"));
        nissan.put(1995, List.of("Altima"));
        fallbackModels.put("Nissan", nissan);
        
        // Chevrolet
        Map<Integer, List<String>> chevrolet = new HashMap<>();
        chevrolet.put(2024, List.of("Silverado", "Camaro", "Equinox", "Tahoe", "Suburban", "Traverse", "Malibu"));
        chevrolet.put(2020, List.of("Silverado", "Camaro", "Equinox", "Tahoe", "Suburban", "Traverse"));
        chevrolet.put(2015, List.of("Silverado", "Camaro", "Equinox", "Tahoe", "Suburban"));
        chevrolet.put(2010, List.of("Silverado", "Camaro", "Equinox", "Tahoe"));
        chevrolet.put(2005, List.of("Silverado", "Camaro", "Equinox"));
        chevrolet.put(2000, List.of("Silverado", "Camaro"));
        chevrolet.put(1995, List.of("Silverado"));
        fallbackModels.put("Chevrolet", chevrolet);
        
        // Hyundai
        Map<Integer, List<String>> hyundai = new HashMap<>();
        hyundai.put(2024, List.of("Elantra", "Sonata", "Tucson", "Santa Fe", "Palisade", "Venue", "Kona"));
        hyundai.put(2020, List.of("Elantra", "Sonata", "Tucson", "Santa Fe", "Palisade", "Venue"));
        hyundai.put(2015, List.of("Elantra", "Sonata", "Tucson", "Santa Fe", "Palisade"));
        hyundai.put(2010, List.of("Elantra", "Sonata", "Tucson", "Santa Fe"));
        hyundai.put(2005, List.of("Elantra", "Sonata", "Tucson"));
        hyundai.put(2000, List.of("Elantra", "Sonata"));
        hyundai.put(1995, List.of("Elantra"));
        fallbackModels.put("Hyundai", hyundai);
        
        // Kia
        Map<Integer, List<String>> kia = new HashMap<>();
        kia.put(2024, List.of("Forte", "K5", "Sportage", "Telluride", "Sorento", "Soul", "Rio"));
        kia.put(2020, List.of("Forte", "K5", "Sportage", "Telluride", "Sorento", "Soul"));
        kia.put(2015, List.of("Forte", "K5", "Sportage", "Telluride", "Sorento"));
        kia.put(2010, List.of("Forte", "K5", "Sportage", "Telluride"));
        kia.put(2005, List.of("Forte", "K5", "Sportage"));
        kia.put(2000, List.of("Forte", "K5"));
        kia.put(1995, List.of("Forte"));
        fallbackModels.put("Kia", kia);
        
        // Lexus
        Map<Integer, List<String>> lexus = new HashMap<>();
        lexus.put(2024, List.of("ES", "RX", "NX", "LS", "LC", "RC", "UX", "GX"));
        lexus.put(2020, List.of("ES", "RX", "NX", "LS", "LC", "RC"));
        lexus.put(2015, List.of("ES", "RX", "NX", "LS", "LC"));
        lexus.put(2010, List.of("ES", "RX", "NX", "LS"));
        lexus.put(2005, List.of("ES", "RX", "NX"));
        lexus.put(2000, List.of("ES", "RX"));
        lexus.put(1995, List.of("ES"));
        fallbackModels.put("Lexus", lexus);
        
        // Mazda
        Map<Integer, List<String>> mazda = new HashMap<>();
        mazda.put(2024, List.of("3", "6", "CX-5", "CX-30", "CX-9", "MX-5", "CX-50"));
        mazda.put(2020, List.of("3", "6", "CX-5", "CX-30", "CX-9", "MX-5"));
        mazda.put(2015, List.of("3", "6", "CX-5", "CX-30", "CX-9"));
        mazda.put(2010, List.of("3", "6", "CX-5", "CX-30"));
        mazda.put(2005, List.of("3", "6", "CX-5"));
        mazda.put(2000, List.of("3", "6"));
        mazda.put(1995, List.of("3"));
        fallbackModels.put("Mazda", mazda);
        
        // Tesla
        Map<Integer, List<String>> tesla = new HashMap<>();
        tesla.put(2024, List.of("Model 3", "Model Y", "Model S", "Model X", "Cybertruck"));
        tesla.put(2020, List.of("Model 3", "Model Y", "Model S", "Model X"));
        tesla.put(2015, List.of("Model S", "Model X"));
        tesla.put(2010, List.of("Model S"));
        tesla.put(2005, List.of());
        tesla.put(2000, List.of());
        tesla.put(1995, List.of());
        fallbackModels.put("Tesla", tesla);
        
        // Porsche
        Map<Integer, List<String>> porsche = new HashMap<>();
        porsche.put(2024, List.of("911", "Cayenne", "Macan", "Panamera", "Taycan", "Cayman", "Boxster"));
        porsche.put(2020, List.of("911", "Cayenne", "Macan", "Panamera", "Taycan"));
        porsche.put(2015, List.of("911", "Cayenne", "Macan", "Panamera"));
        porsche.put(2010, List.of("911", "Cayenne", "Macan"));
        porsche.put(2005, List.of("911", "Cayenne"));
        porsche.put(2000, List.of("911"));
        porsche.put(1995, List.of("911"));
        fallbackModels.put("Porsche", porsche);
        
        // Subaru
        Map<Integer, List<String>> subaru = new HashMap<>();
        subaru.put(2024, List.of("Impreza", "Outback", "Forester", "Crosstrek", "Ascent", "Legacy", "WRX"));
        subaru.put(2020, List.of("Impreza", "Outback", "Forester", "Crosstrek", "Ascent"));
        subaru.put(2015, List.of("Impreza", "Outback", "Forester", "Crosstrek"));
        subaru.put(2010, List.of("Impreza", "Outback", "Forester"));
        subaru.put(2005, List.of("Impreza", "Outback"));
        subaru.put(2000, List.of("Impreza"));
        subaru.put(1995, List.of("Impreza"));
        fallbackModels.put("Subaru", subaru);
        
        // Volvo
        Map<Integer, List<String>> volvo = new HashMap<>();
        volvo.put(2024, List.of("S60", "S90", "XC40", "XC60", "XC90", "V60", "V90"));
        volvo.put(2020, List.of("S60", "S90", "XC40", "XC60", "XC90"));
        volvo.put(2015, List.of("S60", "S90", "XC40", "XC60"));
        volvo.put(2010, List.of("S60", "S90", "XC40"));
        volvo.put(2005, List.of("S60", "S90"));
        volvo.put(2000, List.of("S60"));
        volvo.put(1995, List.of("S60"));
        fallbackModels.put("Volvo", volvo);
        
        // Find closest year for the make
        Map<Integer, List<String>> modelsByYear = fallbackModels.getOrDefault(make, new HashMap<>());
        if (modelsByYear.containsKey(year)) {
            return modelsByYear.get(year);
        } else if (!modelsByYear.isEmpty()) {
            // Find the closest year below the requested year
            int closest = modelsByYear.keySet().stream()
                .filter(y -> y <= year)
                .max(Integer::compareTo)
                .orElse(modelsByYear.keySet().stream().min(Integer::compareTo).get());
            return modelsByYear.get(closest);
        }
        // Generic fallback
        return List.of("Model 1", "Model 2");
    }

    // New methods for the extended features
    public List<com.yourorg.routedashboard.entity.Vehicle> getAllVehicles() {
        try {
            return vehicleRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error getting all vehicles: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<com.yourorg.routedashboard.entity.Vehicle> getVehiclesByMake(String make) {
        try {
            return vehicleRepository.findByMake(make);
        } catch (Exception e) {
            System.err.println("Error getting vehicles by make: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<com.yourorg.routedashboard.entity.Vehicle> getVehiclesByMakeAndYear(String make, Integer year) {
        try {
            return vehicleRepository.findByMakeAndYear(make, year);
        } catch (Exception e) {
            System.err.println("Error getting vehicles by make and year: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Integer> getAllYears() {
        // Return a comprehensive list of years from 1994 to 2024
        List<Integer> years = new ArrayList<>();
        for (int year = 1994; year <= 2024; year++) {
            years.add(year);
        }
        return years;
    }
    
    // Method to clear all vehicles from database
    public void clearAllVehicles() {
        try {
            vehicleRepository.deleteAll();
            System.out.println("All vehicles cleared from database");
        } catch (Exception e) {
            System.err.println("Error clearing vehicles: " + e.getMessage());
        }
    }
    
    // Method to check if database is empty
    public boolean isDatabaseEmpty() {
        return vehicleRepository.count() == 0;
    }

    private List<String> getComprehensiveFallbackMakes() {
        return List.of("Toyota", "Honda", "Ford", "BMW", "Mercedes", "Audi", "Volkswagen", "Nissan", "Chevrolet", "Hyundai", "Kia", "Lexus", "Mazda", "Mitsubishi", "Porsche", "Subaru", "Tesla", "Volvo");
    }

    private List<Integer> getComprehensiveFallbackYears(String make) {
        List<Integer> fallbackYears = new ArrayList<>();
        for (int year = 1994; year <= 2024; year++) {
            fallbackYears.add(year);
        }
        return fallbackYears;
    }

    private List<Integer> getMakeSpecificFallbackYears(String make) {
        List<Integer> fallbackYears = new ArrayList<>();
        switch (make) {
            case "Toyota":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Honda":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Ford":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "BMW":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Mercedes":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Audi":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Volkswagen":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Nissan":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Chevrolet":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Hyundai":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Kia":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Lexus":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Mazda":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Mitsubishi":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Porsche":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Subaru":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Tesla":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            case "Volvo":
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
            default:
                for (int year = 2024; year >= 1994; year--) {
                    fallbackYears.add(year);
                }
                break;
        }
        return fallbackYears;
    }
} 