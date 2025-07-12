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
    
    @Value("${openweather.api.key:YOUR_OPENWEATHER_API_KEY}")
    private String openWeatherApiKey;
    
    // Explicit constructor
    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getWeatherData(String city) {
        // Check if API key is valid
        if (openWeatherApiKey == null || openWeatherApiKey.equals("YOUR_OPENWEATHER_API_KEY") || openWeatherApiKey.trim().isEmpty()) {
            System.out.println("OpenWeatherMap API key not configured, using fallback weather data");
            return getFallbackWeatherData(city);
        }
        
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
        
        // Fallback to varied weather if API fails
        return getFallbackWeatherData(city);
    }
    
    private String getFallbackWeatherData(String city) {
        // Generate varied weather data based on city name
        String cityLower = city.toLowerCase().trim();
        int cityHash = Math.abs(cityLower.hashCode());
        
        // Different weather patterns based on city hash
        String[] weatherTypes = {
            "Sunny", "Cloudy", "Partly cloudy", "Overcast", "Light rain", 
            "Heavy rain", "Snow", "Foggy", "Clear", "Stormy"
        };
        
        String[] descriptions = {
            "clear sky", "scattered clouds", "broken clouds", "overcast clouds",
            "light rain", "moderate rain", "heavy rain", "light snow", "fog", "thunderstorm"
        };
        
        int weatherIndex = cityHash % weatherTypes.length;
        int descIndex = cityHash % descriptions.length;
        
        // Generate temperature based on city hash (between -10 and 35°C)
        double temperature = -10.0 + (cityHash % 45);
        
        // Generate humidity based on weather type
        int humidity;
        if (weatherTypes[weatherIndex].toLowerCase().contains("rain") || 
            weatherTypes[weatherIndex].toLowerCase().contains("snow")) {
            humidity = 70 + (cityHash % 30); // 70-100% for precipitation
        } else if (weatherTypes[weatherIndex].toLowerCase().contains("sunny") || 
                   weatherTypes[weatherIndex].toLowerCase().contains("clear")) {
            humidity = 30 + (cityHash % 40); // 30-70% for clear weather
        } else {
            humidity = 50 + (cityHash % 30); // 50-80% for cloudy weather
        }
        
        return String.format("%s, %.1f°C, %d%% humidity", 
            descriptions[descIndex], temperature, humidity);
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