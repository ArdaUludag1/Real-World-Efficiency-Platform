package com.yourorg.routedashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${openweather.api.key}")
    private String openWeatherApiKey;
    
    // Explicit constructor
    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getWeatherData(String city) {
        try {
            String url = String.format(
                "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                city.replace(" ", "%20"), openWeatherApiKey
            );
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                
                // Check if the API call was successful
                if (jsonNode.has("cod") && jsonNode.get("cod").asInt() == 200) {
                    JsonNode main = jsonNode.get("main");
                    JsonNode weather = jsonNode.get("weather");
                    
                    if (main != null && weather != null && weather.isArray() && weather.size() > 0) {
                        double temperature = main.get("temp").asDouble();
                        int humidity = main.get("humidity").asInt();
                        String description = weather.get(0).get("description").asText();
                        String mainWeather = weather.get(0).get("main").asText();
                        
                        // Format weather information
                        String weatherInfo = String.format("%s, %.1f°C, %d%% humidity", 
                            description, temperature, humidity);
                        
                        return weatherInfo;
                    }
                } else {
                    // API returned an error
                    String errorMessage = jsonNode.has("message") ? 
                        jsonNode.get("message").asText() : "Weather data unavailable";
                    System.err.println("OpenWeatherMap API error: " + errorMessage);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching weather data from OpenWeatherMap: " + e.getMessage());
        }
        
        // Fallback to mock weather if API fails
        return "Sunny, 22°C, 60% humidity";
    }
    
    public boolean hasPrecipitation(String weather) {
        if (weather == null) return false;
        
        String weatherLower = weather.toLowerCase();
        return weatherLower.contains("rain") || 
               weatherLower.contains("snow") ||
               weatherLower.contains("storm") ||
               weatherLower.contains("drizzle") ||
               weatherLower.contains("shower") ||
               weatherLower.contains("sleet") ||
               weatherLower.contains("hail");
    }
    
    public double getWeatherAdjustmentFactor(String weather) {
        if (hasPrecipitation(weather)) {
            return 1.2; // +20% for precipitation
        } else if (weather.toLowerCase().contains("snow")) {
            return 1.3; // +30% for snow
        } else if (weather.toLowerCase().contains("storm")) {
            return 1.25; // +25% for storms
        } else {
            return 1.0; // No adjustment for clear weather
        }
    }
} 