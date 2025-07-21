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
import java.util.Random;
import com.yourorg.routedashboard.entity.Vehicle;

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
        List<String> makes = tryGetMakesFromMultipleSources();
        if (!makes.isEmpty()) {
            System.out.println("CarQueryAPI SUCCESS: Returning makes from CarQueryAPI");
            return makes;
        }
        System.out.println("CarQueryAPI FAILURE: Using backup database for makes.");
        return vehicleRepository.findAllMakes();
    }

    private List<String> tryGetMakesFromMultipleSources() {
        // Try different API endpoints and approaches
        String[] endpoints = {
            "https://www.carqueryapi.com/api/0.3/?cmd=getMakes",
            "https://www.carqueryapi.com/api/0.3/?cmd=getMakes&callback=test",
            "https://www.carqueryapi.com/api/0.3/?cmd=getMakes&format=json",
            "https://www.carqueryapi.com/api/0.3/?cmd=getMakes&sold_in_us=1",
            "https://www.carqueryapi.com/api/0.3/?cmd=getMakes&sold_in_us=0",
            "https://www.carqueryapi.com/api/0.3/?cmd=getMakes&sold_in_us=1&format=json",
            "https://www.carqueryapi.com/api/0.3/?cmd=getMakes&sold_in_us=0&format=json"
        };
        
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Edge/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/121.0"
        };
        
        Random random = new Random();
        int totalAttempts = 0;
        int maxTotalAttempts = 6; // Reduced attempts for faster fallback
        
        for (String endpoint : endpoints) {
            for (int attempt = 0; attempt < 8 && totalAttempts < maxTotalAttempts; attempt++) {
                totalAttempts++;
                try {
                    HttpHeaders headers = createAdvancedHeaders(userAgents[random.nextInt(userAgents.length)]);
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    
                    System.out.println("Trying endpoint: " + endpoint + " (attempt " + attempt + ", total: " + totalAttempts + ")");
                    
                    ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);
                    
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        String body = response.getBody();
                        
                        // Log the raw response for debugging
                        System.out.println("Raw response from " + endpoint + ": " + body.substring(0, Math.min(200, body.length())));
                        
                        // Check for HTML or error page
                        if (body.trim().startsWith("<")) {
                            System.out.println("Received HTML response, likely an error page. Skipping...");
                            continue;
                        }
                        
                        // Check for access denied error
                        if (body.contains("access has been denied") || body.contains("CarQuery API access has been denied")) {
                            System.out.println("API access denied, trying next attempt...");
                            // Longer delay for access denied
                            try { Thread.sleep(5000 + random.nextInt(10000)); } catch (InterruptedException ie) { break; }
                            continue;
                        }
                        
                        // Remove JSONP wrapper if present
                        String json = body;
                        if (json.trim().startsWith("callback(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        } else if (json.trim().startsWith("test(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        }
                        
                        // Try to parse JSON response
                        try {
                            JsonNode jsonNode = objectMapper.readTree(json);
                            JsonNode makesNode = jsonNode.get("Makes");
                            if (makesNode == null) makesNode = jsonNode.get("makes");
                            
                            List<String> makes = new ArrayList<>();
                            if (makesNode != null && makesNode.isArray()) {
                                for (JsonNode make : makesNode) {
                                    String makeName = make.get("make_display").asText();
                                    if (makeName != null && !makeName.isEmpty()) {
                                        makes.add(makeName);
                                    }
                                }
                            }
                            
                            if (!makes.isEmpty()) {
                                System.out.println("Successfully found " + makes.size() + " makes from " + endpoint);
                                return makes;
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing JSON from " + endpoint + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("HTTP error: " + response.getStatusCode() + " for " + endpoint);
                    }
                    
                    // Longer random delay between attempts
                    try { Thread.sleep(3000 + random.nextInt(5000)); } catch (InterruptedException ie) { break; }
                    
                } catch (Exception e) {
                    System.err.println("Error calling " + endpoint + ": " + e.getMessage());
                }
            }
        }
        
        return new ArrayList<>();
    }

    private HttpHeaders createAdvancedHeaders(String userAgent) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", userAgent);
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        headers.set("Referer", "https://www.carqueryapi.com/");
        headers.set("Origin", "https://www.carqueryapi.com");
        headers.set("Cache-Control", "no-cache");
        headers.set("Pragma", "no-cache");
        headers.set("Sec-Fetch-Dest", "empty");
        headers.set("Sec-Fetch-Mode", "cors");
        headers.set("Sec-Fetch-Site", "same-origin");
        headers.set("DNT", "1");
        headers.set("Connection", "keep-alive");
        headers.set("Upgrade-Insecure-Requests", "1");
        headers.set("Sec-Ch-Ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"");
        headers.set("Sec-Ch-Ua-Mobile", "?0");
        headers.set("Sec-Ch-Ua-Platform", "\"Windows\"");
        return headers;
    }

    private List<String> getFallbackMakes() {
        List<String> fallbackMakes = new ArrayList<>();
        fallbackMakes.add("Acura");
        fallbackMakes.add("Alfa Romeo");
        fallbackMakes.add("Aston Martin");
        fallbackMakes.add("Audi");
        fallbackMakes.add("Bentley");
        fallbackMakes.add("BMW");
        fallbackMakes.add("Buick");
        fallbackMakes.add("Cadillac");
        fallbackMakes.add("Chevrolet");
        fallbackMakes.add("Chrysler");
        fallbackMakes.add("Citroen");
        fallbackMakes.add("Dodge");
        fallbackMakes.add("Ferrari");
        fallbackMakes.add("Fiat");
        fallbackMakes.add("Ford");
        fallbackMakes.add("Genesis");
        fallbackMakes.add("GMC");
        fallbackMakes.add("Honda");
        fallbackMakes.add("Hyundai");
        fallbackMakes.add("Infiniti");
        fallbackMakes.add("Jaguar");
        fallbackMakes.add("Jeep");
        fallbackMakes.add("Kia");
        fallbackMakes.add("Lamborghini");
        fallbackMakes.add("Land Rover");
        fallbackMakes.add("Lexus");
        fallbackMakes.add("Lincoln");
        fallbackMakes.add("Lotus");
        fallbackMakes.add("Maserati");
        fallbackMakes.add("Mazda");
        fallbackMakes.add("McLaren");
        fallbackMakes.add("Mercedes-Benz");
        fallbackMakes.add("MINI");
        fallbackMakes.add("Mitsubishi");
        fallbackMakes.add("Nissan");
        fallbackMakes.add("Oldsmobile");
        fallbackMakes.add("Peugeot");
        fallbackMakes.add("Pontiac");
        fallbackMakes.add("Porsche");
        fallbackMakes.add("Ram");
        fallbackMakes.add("Renault");
        fallbackMakes.add("Rolls-Royce");
        fallbackMakes.add("Saab");
        fallbackMakes.add("Saturn");
        fallbackMakes.add("Scion");
        fallbackMakes.add("Seat");
        fallbackMakes.add("Skoda");
        fallbackMakes.add("Smart");
        fallbackMakes.add("Subaru");
        fallbackMakes.add("Suzuki");
        fallbackMakes.add("Tesla");
        fallbackMakes.add("Toyota");
        fallbackMakes.add("Volkswagen");
        fallbackMakes.add("Volvo");
        return fallbackMakes;
    }

    public List<String> getMakesByYear(Integer year) {
        List<String> makes = tryGetMakesByYearFromMultipleSources(year);
        if (!makes.isEmpty()) {
            System.out.println("CarQueryAPI SUCCESS: Returning makes for year " + year + " from CarQueryAPI");
            return makes;
        }
        System.out.println("CarQueryAPI FAILURE: Using backup database for makes for year " + year);
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getYear().equals(year))
                .map(v -> v.getMake())
                .distinct()
                .sorted()
                .toList();
    }

    private List<String> tryGetMakesByYearFromMultipleSources(Integer year) {
        String[] endpoints = {
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getMakes&year=%d", year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getMakes&year=%d&callback=test", year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getMakes&year=%d&format=json", year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getMakes&year=%d&sold_in_us=1", year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getMakes&year=%d&sold_in_us=0", year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getMakes&year=%d&sold_in_us=1&format=json", year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getMakes&year=%d&sold_in_us=0&format=json", year)
        };
        
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Edge/120.0.0.0 Safari/537.36"
        };
        
        Random random = new Random();
        int totalAttempts = 0;
        int maxTotalAttempts = 6; // Reduced attempts for faster fallback
        
        for (String endpoint : endpoints) {
            for (int attempt = 0; attempt < 8 && totalAttempts < maxTotalAttempts; attempt++) {
                totalAttempts++;
                try {
                    HttpHeaders headers = createAdvancedHeaders(userAgents[random.nextInt(userAgents.length)]);
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    
                    System.out.println("Trying endpoint for year " + year + ": " + endpoint + " (attempt " + attempt + ", total: " + totalAttempts + ")");
                    
                    ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);
                    
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        String body = response.getBody();
                        
                        // Log the raw response for debugging
                        System.out.println("Raw response from " + endpoint + ": " + body.substring(0, Math.min(200, body.length())));
                        
                        // Check for HTML or error page
                        if (body.trim().startsWith("<")) {
                            System.out.println("Received HTML response, likely an error page. Skipping...");
                            continue;
                        }
                        
                        // Check for access denied error
                        if (body.contains("access has been denied") || body.contains("CarQuery API access has been denied")) {
                            System.out.println("API access denied, trying next attempt...");
                            // Longer delay for access denied
                            try { Thread.sleep(5000 + random.nextInt(10000)); } catch (InterruptedException ie) { break; }
                            continue;
                        }
                        
                        // Remove JSONP wrapper if present
                        String json = body;
                        if (json.trim().startsWith("callback(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        } else if (json.trim().startsWith("test(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        }
                        
                        try {
                            JsonNode jsonNode = objectMapper.readTree(json);
                            JsonNode makesNode = jsonNode.get("Makes");
                            if (makesNode == null) makesNode = jsonNode.get("makes");
                            List<String> makes = new ArrayList<>();
                            if (makesNode != null && makesNode.isArray()) {
                                for (JsonNode make : makesNode) {
                                    String makeName = make.get("make_display").asText();
                                    if (makeName != null && !makeName.isEmpty()) {
                                        makes.add(makeName);
                                    }
                                }
                            }
                            
                            if (!makes.isEmpty()) {
                                System.out.println("Successfully found " + makes.size() + " makes for year " + year + " from " + endpoint);
                                return makes;
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing JSON from " + endpoint + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("HTTP error: " + response.getStatusCode() + " for " + endpoint);
                    }
                    
                    // Longer random delay between attempts
                    try { Thread.sleep(3000 + random.nextInt(5000)); } catch (InterruptedException ie) { break; }
                    
                } catch (Exception e) {
                    System.err.println("Error calling " + endpoint + ": " + e.getMessage());
                }
            }
        }
        
        return new ArrayList<>();
    }

    private List<String> getFallbackMakesByYear(Integer year) {
        // Return all makes for recent years, fewer for older years
        List<String> allMakes = getFallbackMakes();
        
        if (year >= 2020) {
            return allMakes; // Most makes available in recent years
        } else if (year >= 2010) {
            // Filter to major manufacturers
            return allMakes.stream()
                .filter(make -> !make.equals("Tesla") && !make.equals("Genesis"))
                .collect(java.util.stream.Collectors.toList());
        } else if (year >= 2000) {
            // Filter to established manufacturers
            return allMakes.stream()
                .filter(make -> !make.equals("Tesla") && !make.equals("Genesis") && 
                               !make.equals("Scion") && !make.equals("Saturn"))
                .collect(java.util.stream.Collectors.toList());
        } else {
            // Very limited selection for older years
            List<String> oldMakes = new ArrayList<>();
            oldMakes.add("Chevrolet");
            oldMakes.add("Ford");
            oldMakes.add("Dodge");
            oldMakes.add("Chrysler");
            oldMakes.add("Buick");
            oldMakes.add("Cadillac");
            oldMakes.add("Pontiac");
            oldMakes.add("Oldsmobile");
            oldMakes.add("Plymouth");
            oldMakes.add("Mercury");
            return oldMakes;
        }
    }

    public List<String> getModelsByYearAndMake(Integer year, String make) {
        List<String> models = tryGetModelsFromMultipleSources(year, make);
        if (!models.isEmpty()) {
            System.out.println("CarQueryAPI SUCCESS: Returning models for " + make + " " + year + " from CarQueryAPI");
            return models;
        }
        System.out.println("CarQueryAPI FAILURE: Using backup database for models for " + make + " " + year);
        return vehicleRepository.findModelsByMakeAndYear(make, year);
    }

    private List<String> tryGetModelsFromMultipleSources(Integer year, String make) {
        String[] endpoints = {
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getModels&year=%d&make=%s", year, make.replace(" ", "%20")),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getModels&make=%s&year=%d", make.replace(" ", "%20"), year)
        };
        
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        };
        
        Random random = new Random();
        int totalAttempts = 0;
        int maxTotalAttempts = 6; // Reduced attempts for faster fallback
        
        for (String endpoint : endpoints) {
            for (int attempt = 0; attempt < 25 && totalAttempts < maxTotalAttempts; attempt++) {
                totalAttempts++;
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("User-Agent", userAgents[random.nextInt(userAgents.length)]);
                    headers.set("Accept", "application/json, text/plain, */*");
                    headers.set("Accept-Language", "en-US,en;q=0.9");
                    headers.set("Referer", "https://www.carqueryapi.com/");
                    headers.set("Cache-Control", "no-cache");
                    headers.set("Pragma", "no-cache");
                    
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    
                    System.out.println("Trying endpoint for models: " + endpoint + " (attempt " + attempt + ", total: " + totalAttempts + ")");
                    
                    ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);
                    
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        String body = response.getBody();
                        
                        // Log the raw response for debugging
                        System.out.println("Raw response from " + endpoint + ": " + body.substring(0, Math.min(200, body.length())));
                        
                        // Check for HTML or error page
                        if (body.trim().startsWith("<")) {
                            System.out.println("Received HTML response, likely an error page. Skipping...");
                            continue;
                        }
                        
                        // Check for access denied error
                        if (body.contains("access has been denied") || body.contains("CarQuery API access has been denied")) {
                            System.out.println("API access denied, trying next attempt...");
                            try { Thread.sleep(1000 + random.nextInt(2000)); } catch (InterruptedException ie) { break; }
                            continue;
                        }
                        
                        // Remove JSONP wrapper if present
                        String json = body;
                        if (json.trim().startsWith("callback(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        } else if (json.trim().startsWith("test(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        }
                        
                        try {
                            JsonNode jsonNode = objectMapper.readTree(json);
                            JsonNode modelsNode = jsonNode.get("Models");
                            if (modelsNode == null) modelsNode = jsonNode.get("models");
                            List<String> models = new ArrayList<>();
                            if (modelsNode != null && modelsNode.isArray()) {
                                for (JsonNode model : modelsNode) {
                                    String modelName = model.get("model_name").asText();
                                    if (modelName != null && !modelName.isEmpty()) {
                                        models.add(modelName);
                                    }
                                }
                            }
                            if (!models.isEmpty()) {
                                System.out.println("Found " + models.size() + " models for " + make + " " + year + " from " + endpoint);
                                return models;
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing JSON from " + endpoint + ": " + e.getMessage());
                        }
                    }
                    
                    try { Thread.sleep(500 + random.nextInt(1500)); } catch (InterruptedException ie) { break; }
                    
                } catch (Exception e) {
                    System.err.println("Error calling " + endpoint + ": " + e.getMessage());
                }
            }
        }
        
        return new ArrayList<>();
    }

    public List<String> getTrimsByMakeModelYear(String make, String model, Integer year) {
        List<String> trims = tryGetTrimsFromMultipleSources(make, model, year);
        if (!trims.isEmpty()) {
            System.out.println("CarQueryAPI SUCCESS: Returning trims for " + make + " " + model + " " + year + " from CarQueryAPI");
            return trims;
        }
        System.out.println("CarQueryAPI FAILURE: Using backup database for trims for " + make + " " + model + " " + year);
        return vehicleRepository.findTrimsByMakeModelYear(make, model, year);
    }

    private List<String> tryGetTrimsFromMultipleSources(String make, String model, Integer year) {
        String[] endpoints = {
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getTrims&make=%s&model=%s&year=%d", make.replace(" ", "%20"), model.replace(" ", "%20"), year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getTrims&year=%d&make=%s&model=%s", year, make.replace(" ", "%20"), model.replace(" ", "%20"))
        };
        
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        };
        
        Random random = new Random();
        int totalAttempts = 0;
        int maxTotalAttempts = 6; // Reduced attempts for faster fallback
        
        for (String endpoint : endpoints) {
            for (int attempt = 0; attempt < 25 && totalAttempts < maxTotalAttempts; attempt++) {
                totalAttempts++;
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("User-Agent", userAgents[random.nextInt(userAgents.length)]);
                    headers.set("Accept", "application/json, text/plain, */*");
                    headers.set("Accept-Language", "en-US,en;q=0.9");
                    headers.set("Referer", "https://www.carqueryapi.com/");
                    headers.set("Cache-Control", "no-cache");
                    headers.set("Pragma", "no-cache");
                    
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    
                    System.out.println("Trying endpoint for trims: " + endpoint + " (attempt " + attempt + ", total: " + totalAttempts + ")");
                    
                    ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);
                    
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        String body = response.getBody();
                        
                        // Log the raw response for debugging
                        System.out.println("Raw response from " + endpoint + ": " + body.substring(0, Math.min(200, body.length())));
                        
                        // Check for HTML or error page
                        if (body.trim().startsWith("<")) {
                            System.out.println("Received HTML response, likely an error page. Skipping...");
                            continue;
                        }
                        
                        // Check for access denied error
                        if (body.contains("access has been denied") || body.contains("CarQuery API access has been denied")) {
                            System.out.println("API access denied, trying next attempt...");
                            try { Thread.sleep(1000 + random.nextInt(2000)); } catch (InterruptedException ie) { break; }
                            continue;
                        }
                        
                        // Remove JSONP wrapper if present
                        String json = body;
                        if (json.trim().startsWith("callback(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        } else if (json.trim().startsWith("test(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        }
                        
                        try {
                            JsonNode jsonNode = objectMapper.readTree(json);
                            JsonNode trimsNode = jsonNode.get("Trims");
                            if (trimsNode == null) trimsNode = jsonNode.get("trims");
                            List<String> trims = new ArrayList<>();
                            if (trimsNode != null && trimsNode.isArray()) {
                                for (JsonNode trim : trimsNode) {
                                    String trimName = trim.get("model_trim").asText();
                                    if (trimName != null && !trimName.isEmpty()) {
                                        trims.add(trimName);
                                    }
                                }
                            }
                            if (!trims.isEmpty()) {
                                System.out.println("Found " + trims.size() + " trims for " + make + " " + model + " " + year + " from " + endpoint);
                                return trims;
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing JSON from " + endpoint + ": " + e.getMessage());
                        }
                    }
                    
                    try { Thread.sleep(500 + random.nextInt(1500)); } catch (InterruptedException ie) { break; }
                    
                } catch (Exception e) {
                    System.err.println("Error calling " + endpoint + ": " + e.getMessage());
                }
            }
        }
        
        return new ArrayList<>();
    }

    public Map<String, Object> getVehicleDetails(String make, String model, String trim, Integer year) {
        String[] endpoints = {
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getTrims&make=%s&model=%s&year=%d", make.replace(" ", "%20"), model.replace(" ", "%20"), year),
            String.format("https://www.carqueryapi.com/api/0.3/?cmd=getTrims&year=%d&make=%s&model=%s", year, make.replace(" ", "%20"), model.replace(" ", "%20"))
        };
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        };
        Random random = new Random();
        int totalAttempts = 0;
        int maxTotalAttempts = 6;
        for (String endpoint : endpoints) {
            for (int attempt = 0; attempt < 25 && totalAttempts < maxTotalAttempts; attempt++) {
                totalAttempts++;
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("User-Agent", userAgents[random.nextInt(userAgents.length)]);
                    headers.set("Accept", "application/json, text/plain, */*");
                    headers.set("Accept-Language", "en-US,en;q=0.9");
                    headers.set("Referer", "https://www.carqueryapi.com/");
                    headers.set("Cache-Control", "no-cache");
                    headers.set("Pragma", "no-cache");
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    System.out.println("Trying endpoint for vehicle details: " + endpoint + " (attempt " + attempt + ", total: " + totalAttempts + ")");
                    ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        String body = response.getBody();
                        System.out.println("Raw response from " + endpoint + ": " + body.substring(0, Math.min(200, body.length())));
                        if (body.trim().startsWith("<") || (!body.trim().startsWith("{") && !body.trim().startsWith("[") && !body.trim().matches("[a-zA-Z0-9_\\(\\)\\{\\}\\[\\]\\\"\\':,\\.\\s-]+"))) {
                            System.out.println("CarQueryAPI FAILURE: Received HTML, binary, or unreadable response. Skipping...");
                            continue;
                        }
                        if (body.contains("access has been denied") || body.contains("CarQuery API access has been denied")) {
                            System.out.println("CarQueryAPI FAILURE: API access denied, trying next attempt...");
                            try { Thread.sleep(2000 + random.nextInt(2000)); } catch (InterruptedException ie) { break; }
                            continue;
                        }
                        String json = body;
                        if (json.trim().startsWith("callback(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        } else if (json.trim().startsWith("test(")) {
                            int start = json.indexOf('(') + 1;
                            int end = json.lastIndexOf(')');
                            if (start > 0 && end > start) {
                                json = json.substring(start, end);
                            }
                        }
                        try {
                            JsonNode jsonNode = objectMapper.readTree(json);
                            JsonNode trimsNode = jsonNode.get("Trims");
                            if (trimsNode == null) trimsNode = jsonNode.get("trims");
                            if (trimsNode != null && trimsNode.isArray()) {
                                for (JsonNode trimNode : trimsNode) {
                                    // Match by make, model, trim, year (case-insensitive, fallback to partial match if needed)
                                    boolean match = true;
                                    if (trimNode.has("model_trim") && trim != null && !trim.isEmpty()) {
                                        String apiTrim = trimNode.get("model_trim").asText("");
                                        match &= apiTrim.equalsIgnoreCase(trim);
                                    }
                                    if (trimNode.has("model_name") && model != null && !model.isEmpty()) {
                                        String apiModel = trimNode.get("model_name").asText("");
                                        match &= apiModel.equalsIgnoreCase(model);
                                    }
                                    if (trimNode.has("make_display") && make != null && !make.isEmpty()) {
                                        String apiMake = trimNode.get("make_display").asText("");
                                        match &= apiMake.equalsIgnoreCase(make);
                                    }
                                    if (trimNode.has("model_year")) {
                                        int apiYear = trimNode.get("model_year").asInt(-1);
                                        match &= (apiYear == year);
                                    }
                                    if (match) {
                                        System.out.println("Matched trimNode: " + trimNode.toPrettyString());
                                        // Build or update Vehicle entity from API data
                                        Vehicle vehicle = vehicleRepository.findByMakeAndModelAndTrimAndYear(make, model, trim, year);
                                        if (vehicle == null) {
                                            vehicle = new com.yourorg.routedashboard.entity.Vehicle();
                                            vehicle.setMake(make);
                                            vehicle.setModel(model);
                                            vehicle.setTrim(trim);
                                            vehicle.setYear(year);
                                        }
                                        vehicle.setModelId(trimNode.path("model_id").asText(null));
                                        vehicle.setModelEnginePosition(trimNode.path("model_engine_position").asText(null));
                                        vehicle.setModelEngineCc(parseIntSafe(trimNode, "model_engine_cc"));
                                        vehicle.setModelEngineCyl(parseIntSafe(trimNode, "model_engine_cyl"));
                                        vehicle.setModelEngineType(trimNode.path("model_engine_type").asText(null));
                                        vehicle.setModelEngineValvesPerCyl(parseIntSafe(trimNode, "model_engine_valves_per_cyl"));
                                        vehicle.setModelEnginePowerPs(parseIntSafe(trimNode, "model_engine_power_ps"));
                                        vehicle.setModelEnginePowerRpm(parseIntSafe(trimNode, "model_engine_power_rpm"));
                                        vehicle.setModelEngineTorqueNm(parseIntSafe(trimNode, "model_engine_torque_nm"));
                                        vehicle.setModelEngineTorqueRpm(parseIntSafe(trimNode, "model_engine_torque_rpm"));
                                        vehicle.setModelEngineBoreMm(parseDoubleSafe(trimNode, "model_engine_bore_mm"));
                                        vehicle.setModelEngineStrokeMm(parseDoubleSafe(trimNode, "model_engine_stroke_mm"));
                                        vehicle.setModelEngineCompression(trimNode.path("model_engine_compression").asText(null));
                                        vehicle.setModelEngineFuel(trimNode.path("model_engine_fuel").asText(null));
                                        vehicle.setModelTopSpeedKph(parseIntSafe(trimNode, "model_top_speed_kph"));
                                        vehicle.setModel0To100Kph(parseDoubleSafe(trimNode, "model_0_to_100_kph"));
                                        vehicle.setModelDrive(trimNode.path("model_drive").asText(null));
                                        vehicle.setModelTransmissionType(trimNode.path("model_transmission_type").asText(null));
                                        vehicle.setModelSeats(parseIntSafe(trimNode, "model_seats"));
                                        vehicle.setModelDoors(parseIntSafe(trimNode, "model_doors"));
                                        vehicle.setModelWeightKg(parseIntSafe(trimNode, "model_weight_kg"));
                                        vehicle.setModelLengthMm(parseIntSafe(trimNode, "model_length_mm"));
                                        vehicle.setModelWidthMm(parseIntSafe(trimNode, "model_width_mm"));
                                        vehicle.setModelHeightMm(parseIntSafe(trimNode, "model_height_mm"));
                                        vehicle.setModelWheelbaseMm(parseIntSafe(trimNode, "model_wheelbase_mm"));
                                        vehicle.setModelLkmHwy(parseDoubleSafe(trimNode, "model_lkm_hwy"));
                                        vehicle.setModelLkmMixed(parseDoubleSafe(trimNode, "model_lkm_mixed"));
                                        vehicle.setModelLkmCity(parseDoubleSafe(trimNode, "model_lkm_city"));
                                        vehicle.setModelFuelCapL(parseDoubleSafe(trimNode, "model_fuel_cap_l"));
                                        vehicle.setModelSoldInUs(parseBooleanSafe(trimNode, "model_sold_in_us"));
                                        vehicle.setModelCo2(parseDoubleSafe(trimNode, "model_co2"));
                                        vehicle.setModelMakeDisplay(trimNode.path("model_make_display").asText(null));
                                        vehicle.setMakeDisplay(trimNode.path("make_display").asText(null));
                                        vehicle.setMakeCountry(trimNode.path("make_country").asText(null));
                                        vehicle.setLastUpdated(java.time.LocalDateTime.now());
                                        vehicleRepository.save(vehicle);
                                        Map<String, Object> details = new HashMap<>();
                                        details.put("make", trimNode.path("make_display").asText(""));
                                        details.put("model", trimNode.path("model_name").asText(""));
                                        details.put("trim", trimNode.path("model_trim").asText(""));
                                        details.put("year", trimNode.path("model_year").asText(""));
                                        details.put("body_type", trimNode.path("model_body").asText("-"));
                                        details.put("doors", trimNode.path("model_doors").asText("-"));
                                        details.put("seats", trimNode.path("model_seats").asText("-"));
                                        details.put("engine_type", trimNode.path("model_engine_type").asText("-"));
                                        details.put("engine_size", trimNode.path("model_engine_cc").asText("-"));
                                        details.put("cylinders", trimNode.path("model_engine_cyl").asText("-"));
                                        details.put("transmission", trimNode.path("model_transmission_type").asText("-"));
                                        details.put("acceleration", trimNode.path("model_0_to_100_kph").asText("-"));
                                        details.put("top_speed", trimNode.path("model_top_speed_kph").asText("-"));
                                        details.put("city_l_per_100km", trimNode.path("model_lkm_city").asText("-"));
                                        details.put("highway_l_per_100km", trimNode.path("model_lkm_highway").asText("-"));
                                        details.put("mixed_l_per_100km", trimNode.path("model_lkm_mixed").asText("-"));
                                        details.put("fuel_capacity", trimNode.path("model_fuel_cap_l").asText("-"));
                                        details.put("length", trimNode.path("model_length_mm").asText("-"));
                                        details.put("width", trimNode.path("model_width_mm").asText("-"));
                                        details.put("height", trimNode.path("model_height_mm").asText("-"));
                                        details.put("wheelbase", trimNode.path("model_wheelbase_mm").asText("-"));
                                        details.put("weight", trimNode.path("model_weight_kg").asText("-"));
                                        details.put("source", "carquery");

                                        // Merge with DB data for missing fields
                                        var vehicleDB = vehicleRepository.findByMakeAndModelAndTrimAndYear(make, model, trim, year);
                                        if (vehicleDB != null) {
                                            // Only update if API value is missing or '-'
                                            if (details.get("city_l_per_100km").equals("-") && vehicleDB.getFuelConsumptionCity() != null) {
                                                details.put("city_l_per_100km", vehicleDB.getFuelConsumptionCity().toString());
                                            }
                                            if (details.get("highway_l_per_100km").equals("-") && vehicleDB.getFuelConsumptionHighway() != null) {
                                                details.put("highway_l_per_100km", vehicleDB.getFuelConsumptionHighway().toString());
                                            }
                                            if (details.get("mixed_l_per_100km").equals("-") && vehicleDB.getFuelConsumptionMixed() != null) {
                                                details.put("mixed_l_per_100km", vehicleDB.getFuelConsumptionMixed().toString());
                                            }
                                            // Add more fields here if you extend your Vehicle entity
                                        }
                                        System.out.println("CarQueryAPI SUCCESS: Returning vehicle details from CarQueryAPI (merged): " + details);
                                        return details;
                                    }
                                }
                                // If no match, log all trims for debugging
                                System.err.println("No exact trim match found. Available trims: " + trimsNode.toString());
                            } else {
                                System.err.println("No 'Trims' or 'trims' array found in response: " + jsonNode.toString());
                            }
                        } catch (Exception e) {
                            System.err.println("CarQueryAPI FAILURE: Error parsing JSON from " + endpoint + ": " + e.getMessage());
                        }
                    }
                    try { Thread.sleep(500 + random.nextInt(1500)); } catch (InterruptedException ie) { break; }
                } catch (Exception e) {
                    System.err.println("CarQueryAPI FAILURE: Error calling " + endpoint + ": " + e.getMessage());
                }
            }
        }
        // If all attempts fail, use backup DB
        System.out.println("CarQueryAPI FAILURE: Using backup database for vehicle details.");
        Map<String, Object> details = new HashMap<>();
        var vehicle = vehicleRepository.findByMakeAndModelAndTrimAndYear(make, model, trim, year);
        if (vehicle != null) {
            details.put("make", vehicle.getMake());
            details.put("model", vehicle.getModel());
            details.put("trim", vehicle.getTrim());
            details.put("year", vehicle.getYear());
            details.put("fuel_consumption_city", vehicle.getFuelConsumptionCity());
            details.put("fuel_consumption_highway", vehicle.getFuelConsumptionHighway());
            details.put("fuel_consumption_mixed", vehicle.getFuelConsumptionMixed());
            details.put("source", "database");
        } else {
            details.put("error", "No vehicle found in backup database.");
            details.put("source", "database");
        }
        return details;
    }

    // Utility methods for safe parsing
    private Integer parseIntSafe(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        try {
            return value != null && !value.isEmpty() ? Integer.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private Double parseDoubleSafe(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        try {
            return value != null && !value.isEmpty() ? Double.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private Boolean parseBooleanSafe(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        if (value == null) return null;
        return value.equals("1") || value.equalsIgnoreCase("true");
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
    
    public Map<String, Object> getVehicleFactoryData(String year, String make, String model) {
        try {
            System.out.println("VehicleService: Fetching factory data for " + year + " " + make + " " + model);
            
            // Try to get vehicle details from CarQueryAPI
            Map<String, Object> vehicleDetails = getVehicleDetails(make, model, "", Integer.parseInt(year));
            
            if (vehicleDetails.containsKey("error")) {
                System.out.println("VehicleService: Could not fetch factory data from API, using fallback");
                return getFallbackFactoryData(year, make, model);
            }
            
            // Extract efficiency data
            Map<String, Object> factoryData = new HashMap<>();
            factoryData.put("year", year);
            factoryData.put("make", make);
            factoryData.put("model", model);
            
            // Get fuel efficiency from vehicle details
            if (vehicleDetails.containsKey("fuel_efficiency")) {
                factoryData.put("fuel_efficiency", vehicleDetails.get("fuel_efficiency"));
            } else {
                // Use fallback efficiency based on vehicle type
                factoryData.put("fuel_efficiency", getFallbackEfficiency(make, model));
            }
            
            // Add other relevant factory data
            if (vehicleDetails.containsKey("engine_type")) {
                factoryData.put("engine_type", vehicleDetails.get("engine_type"));
            }
            if (vehicleDetails.containsKey("transmission")) {
                factoryData.put("transmission", vehicleDetails.get("transmission"));
            }
            if (vehicleDetails.containsKey("body_type")) {
                factoryData.put("body_type", vehicleDetails.get("body_type"));
            }
            
            System.out.println("VehicleService: Factory data retrieved successfully: " + factoryData);
            return factoryData;
            
        } catch (Exception e) {
            System.err.println("VehicleService: Error fetching factory data: " + e.getMessage());
            e.printStackTrace();
            return getFallbackFactoryData(year, make, model);
        }
    }
    
    private Map<String, Object> getFallbackFactoryData(String year, String make, String model) {
        Map<String, Object> fallbackData = new HashMap<>();
        fallbackData.put("year", year);
        fallbackData.put("make", make);
        fallbackData.put("model", model);
        fallbackData.put("fuel_efficiency", getFallbackEfficiency(make, model));
        fallbackData.put("engine_type", "Unknown");
        fallbackData.put("transmission", "Unknown");
        fallbackData.put("body_type", "Unknown");
        fallbackData.put("note", "Factory data estimated based on vehicle type");
        
        return fallbackData;
    }
    
    private double getFallbackEfficiency(String make, String model) {
        // Provide reasonable fallback efficiency based on common vehicle types
        String makeLower = make.toLowerCase();
        String modelLower = model.toLowerCase();
        
        // Luxury/Performance vehicles (higher consumption)
        if (makeLower.contains("bmw") || makeLower.contains("mercedes") || makeLower.contains("audi") || 
            makeLower.contains("lexus") || makeLower.contains("porsche")) {
            return 10.5;
        }
        
        // SUVs and trucks (higher consumption)
        if (modelLower.contains("suv") || modelLower.contains("truck") || modelLower.contains("pickup") ||
            modelLower.contains("explorer") || modelLower.contains("escape") || modelLower.contains("cr-v") ||
            modelLower.contains("rav4") || modelLower.contains("highlander")) {
            return 9.5;
        }
        
        // Compact cars (lower consumption)
        if (modelLower.contains("civic") || modelLower.contains("corolla") || modelLower.contains("focus") ||
            modelLower.contains("sentra") || modelLower.contains("elantra") || modelLower.contains("forte")) {
            return 7.0;
        }
        
        // Hybrid vehicles (very low consumption)
        if (modelLower.contains("hybrid") || modelLower.contains("prius") || modelLower.contains("insight")) {
            return 4.5;
        }
        
        // Electric vehicles (very low consumption)
        if (modelLower.contains("electric") || modelLower.contains("ev") || modelLower.contains("tesla")) {
            return 2.0; // kWh/100km equivalent
        }
        
        // Default for sedans and other vehicles
        return 8.0;
    }
} 