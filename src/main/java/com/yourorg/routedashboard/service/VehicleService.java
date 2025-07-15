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
        headers.set("Accept-Encoding", "gzip, deflate, br");
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
                        // Log the raw response for debugging
                        System.out.println("Raw response from " + endpoint + ": " + body.substring(0, Math.min(200, body.length())));
                        // Check for HTML or error page or unreadable/binary
                        if (body.trim().startsWith("<") || (!body.trim().startsWith("{") && !body.trim().startsWith("[") && !body.trim().matches("[a-zA-Z0-9_\\(\\)\\{\\}\\[\\]\\\"\\':,\\.\\s-]+"))) {
                            System.out.println("CarQueryAPI FAILURE: Received HTML, binary, or unreadable response. Skipping...");
                            continue;
                        }
                        // Check for access denied error
                        if (body.contains("access has been denied") || body.contains("CarQuery API access has been denied")) {
                            System.out.println("CarQueryAPI FAILURE: API access denied, trying next attempt...");
                            try { Thread.sleep(2000 + random.nextInt(2000)); } catch (InterruptedException ie) { break; }
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
                            Map<String, Object> vehicleDetails = new HashMap<>();
                            if (trimsNode != null && trimsNode.isArray()) {
                                for (JsonNode trimNode : trimsNode) {
                                    String trimName = trimNode.get("model_trim").asText();
                                    if (trimName != null && trimName.equals(trim)) {
                                        vehicleDetails.put("make", make);
                                        vehicleDetails.put("model", model);
                                        vehicleDetails.put("trim", trim);
                                        vehicleDetails.put("year", year);
                                        if (trimNode.has("model_lkm_city")) vehicleDetails.put("fuel_consumption_city", trimNode.get("model_lkm_city").asText());
                                        if (trimNode.has("model_lkm_highway")) vehicleDetails.put("fuel_consumption_highway", trimNode.get("model_lkm_highway").asText());
                                        if (trimNode.has("model_lkm_mixed")) vehicleDetails.put("fuel_consumption_mixed", trimNode.get("model_lkm_mixed").asText());
                                        vehicleDetails.put("source", "carquery");
                                        System.out.println("CarQueryAPI SUCCESS: Returning vehicle details from CarQueryAPI");
                                        return vehicleDetails;
                                    }
                                }
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
} 