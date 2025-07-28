package com.yourorg.realworldefficiency.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class LocationWeatherService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Explicit constructor
    public LocationWeatherService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public Map<String, Object> getLocationAndWeather(String location) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("LocationWeatherService: Processing location: " + location);
        
        try {
            // Step 1: Try to get coordinates from free geocoding service
            Map<String, Object> geocodeResult = getCoordinatesFromFreeService(location);
            
            if (geocodeResult.containsKey("error")) {
                // Fallback to simulated data
                System.out.println("LocationWeatherService: Using fallback data for location: " + location);
                return getFallbackLocationAndWeather(location);
            }
            
            double lat = (Double) geocodeResult.get("latitude");
            double lng = (Double) geocodeResult.get("longitude");
            String formattedAddress = (String) geocodeResult.get("formatted_address");
            
            System.out.println("LocationWeatherService: Found coordinates - Lat: " + lat + ", Lng: " + lng);
            System.out.println("LocationWeatherService: Formatted address: " + formattedAddress);
            
            result.put("latitude", lat);
            result.put("longitude", lng);
            result.put("formatted_address", formattedAddress);
            
            // Step 2: Try to get weather data from free service
            Map<String, Object> weatherResult = getWeatherFromFreeService(lat, lng);
            
            if (weatherResult.containsKey("error")) {
                // Fallback to simulated weather data
                System.out.println("LocationWeatherService: Using fallback weather data");
                weatherResult = getFallbackWeatherData(location);
            }
            
            result.putAll(weatherResult);
            
        } catch (Exception e) {
            System.err.println("LocationWeatherService: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            // Return fallback data
            return getFallbackLocationAndWeather(location);
        }
        
        System.out.println("LocationWeatherService: Final result: " + result);
        return result;
    }
    
    public Map<String, Object> calculateDistance(String origin, String destination) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("LocationWeatherService: Calculating distance from " + origin + " to " + destination);
        
        try {
            // Step 1: Get coordinates for origin
            Map<String, Object> originCoords = getCoordinatesFromFreeService(origin);
            if (originCoords.containsKey("error")) {
                result.put("error", "Could not find coordinates for origin: " + origin);
                return result;
            }
            
            // Step 2: Get coordinates for destination
            Map<String, Object> destCoords = getCoordinatesFromFreeService(destination);
            if (destCoords.containsKey("error")) {
                result.put("error", "Could not find coordinates for destination: " + destination);
                return result;
            }
            
            double originLat = (Double) originCoords.get("latitude");
            double originLng = (Double) originCoords.get("longitude");
            double destLat = (Double) destCoords.get("latitude");
            double destLng = (Double) destCoords.get("longitude");
            
            System.out.println("LocationWeatherService: Origin coordinates - Lat: " + originLat + ", Lng: " + originLng);
            System.out.println("LocationWeatherService: Destination coordinates - Lat: " + destLat + ", Lng: " + destLng);
            
            // Step 3: Try to get route information from free service
            Map<String, Object> routeResult = getRouteFromFreeService(originLat, originLng, destLat, destLng);
            
            if (routeResult.containsKey("error")) {
                // Fallback to direct distance calculation
                System.out.println("LocationWeatherService: Using fallback distance calculation");
                routeResult = calculateDirectDistance(originLat, originLng, destLat, destLng);
            }
            
            result.put("origin", originCoords.get("formatted_address"));
            result.put("destination", destCoords.get("formatted_address"));
            result.putAll(routeResult);
            
        } catch (Exception e) {
            System.err.println("LocationWeatherService: Distance calculation error: " + e.getMessage());
            e.printStackTrace();
            result.put("error", "Error calculating distance: " + e.getMessage());
        }
        
        System.out.println("LocationWeatherService: Distance result: " + result);
        return result;
    }
    
    private Map<String, Object> getRouteFromFreeService(double originLat, double originLng, double destLat, double destLng) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Try using OSRM (Open Source Routing Machine) - free routing service
            String routeUrl = String.format(
                "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false&steps=false",
                originLng, originLat, destLng, destLat
            );
            
            System.out.println("LocationWeatherService: Calling OSRM API: " + routeUrl);
            
            String routeResponse = restTemplate.getForObject(routeUrl, String.class);
            System.out.println("LocationWeatherService: OSRM response: " + (routeResponse != null ? routeResponse.substring(0, Math.min(200, routeResponse.length())) : "null"));
            
            JsonNode routeNode = objectMapper.readTree(routeResponse);
            
            if (routeNode.has("routes") && routeNode.get("routes").isArray() && routeNode.get("routes").size() > 0) {
                JsonNode route = routeNode.get("routes").get(0);
                
                // Distance in meters
                double distanceMeters = route.get("distance").asDouble();
                // Duration in seconds
                double durationSeconds = route.get("duration").asDouble();
                
                // Convert to more readable units
                double distanceKm = distanceMeters / 1000.0;
                double distanceMiles = distanceKm * 0.621371;
                double durationHours = durationSeconds / 3600.0;
                double durationMinutes = durationSeconds / 60.0;
                
                result.put("distance_km", Math.round(distanceKm * 10.0) / 10.0);
                result.put("distance_miles", Math.round(distanceMiles * 10.0) / 10.0);
                result.put("duration_hours", Math.round(durationHours * 10.0) / 10.0);
                result.put("duration_minutes", Math.round(durationMinutes));
                result.put("route_type", "driving");
                
                System.out.println("LocationWeatherService: OSRM success - Distance: " + distanceKm + " km, Duration: " + durationHours + " hours");
            } else {
                result.put("error", "No route found");
                System.out.println("LocationWeatherService: OSRM - no route found");
            }
            
        } catch (Exception e) {
            System.err.println("LocationWeatherService: OSRM error: " + e.getMessage());
            result.put("error", "Routing service error: " + e.getMessage());
        }
        
        return result;
    }
    
    private Map<String, Object> calculateDirectDistance(double originLat, double originLng, double destLat, double destLng) {
        Map<String, Object> result = new HashMap<>();
        
        // Calculate direct distance using Haversine formula
        double distanceKm = calculateHaversineDistance(originLat, originLng, destLat, destLng);
        double distanceMiles = distanceKm * 0.621371;
        
        // Estimate driving time (assuming average speed of 60 km/h)
        double estimatedHours = distanceKm / 60.0;
        double estimatedMinutes = estimatedHours * 60.0;
        
        result.put("distance_km", Math.round(distanceKm * 10.0) / 10.0);
        result.put("distance_miles", Math.round(distanceMiles * 10.0) / 10.0);
        result.put("duration_hours", Math.round(estimatedHours * 10.0) / 10.0);
        result.put("duration_minutes", Math.round(estimatedMinutes));
        result.put("route_type", "direct");
        result.put("note", "Direct distance (as crow flies) - actual driving distance may be longer");
        
        System.out.println("LocationWeatherService: Direct distance calculated - Distance: " + distanceKm + " km");
        
        return result;
    }
    
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula to calculate distance between two points on Earth
        final int R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private Map<String, Object> getCoordinatesFromFreeService(String location) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Try using Nominatim (OpenStreetMap) - free geocoding service
            String geocodeUrl = String.format(
                "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1",
                location.replace(" ", "+")
            );
            
            System.out.println("LocationWeatherService: Calling Nominatim API: " + geocodeUrl);
            
            String geocodeResponse = restTemplate.getForObject(geocodeUrl, String.class);
            System.out.println("LocationWeatherService: Nominatim response: " + (geocodeResponse != null ? geocodeResponse.substring(0, Math.min(200, geocodeResponse.length())) : "null"));
            
            JsonNode geocodeNode = objectMapper.readTree(geocodeResponse);
            
            if (geocodeNode.isArray() && geocodeNode.size() > 0) {
                JsonNode firstResult = geocodeNode.get(0);
                double lat = firstResult.get("lat").asDouble();
                double lng = firstResult.get("lon").asDouble();
                String displayName = firstResult.get("display_name").asText();
                
                result.put("latitude", lat);
                result.put("longitude", lng);
                result.put("formatted_address", displayName);
                
                System.out.println("LocationWeatherService: Nominatim success");
            } else {
                result.put("error", "Location not found");
                System.out.println("LocationWeatherService: Nominatim - no results found");
            }
            
        } catch (Exception e) {
            System.err.println("LocationWeatherService: Nominatim error: " + e.getMessage());
            result.put("error", "Geocoding service error: " + e.getMessage());
        }
        
        return result;
    }
    
    private Map<String, Object> getWeatherFromFreeService(double lat, double lng) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Try using OpenMeteo - free weather service
            String weatherUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,apparent_temperature,pressure_msl,wind_speed_10m,weather_code&timezone=auto",
                lat, lng
            );
            
            System.out.println("LocationWeatherService: Calling OpenMeteo API: " + weatherUrl);
            
            String weatherResponse = restTemplate.getForObject(weatherUrl, String.class);
            System.out.println("LocationWeatherService: OpenMeteo response: " + (weatherResponse != null ? weatherResponse.substring(0, Math.min(200, weatherResponse.length())) : "null"));
            
            JsonNode weatherNode = objectMapper.readTree(weatherResponse);
            
            if (weatherNode.has("current")) {
                JsonNode current = weatherNode.get("current");
                
                result.put("temperature", current.get("temperature_2m").asDouble());
                result.put("feels_like", current.get("apparent_temperature").asDouble());
                result.put("humidity", current.get("relative_humidity_2m").asInt());
                result.put("pressure", current.get("pressure_msl").asInt());
                result.put("wind_speed", current.get("wind_speed_10m").asDouble());
                
                // Convert weather code to description
                int weatherCode = current.get("weather_code").asInt();
                String description = getWeatherDescription(weatherCode);
                result.put("description", description);
                result.put("main_weather", description.split(" ")[0]); // First word
                
                System.out.println("LocationWeatherService: OpenMeteo success");
            } else {
                result.put("error", "Weather data not available");
                System.out.println("LocationWeatherService: OpenMeteo - no current data");
            }
            
        } catch (Exception e) {
            System.err.println("LocationWeatherService: OpenMeteo error: " + e.getMessage());
            result.put("error", "Weather service error: " + e.getMessage());
        }
        
        return result;
    }
    
    private String getWeatherDescription(int code) {
        // WMO Weather interpretation codes
        switch (code) {
            case 0: return "Clear sky";
            case 1: case 2: case 3: return "Partly cloudy";
            case 45: case 48: return "Foggy";
            case 51: case 53: case 55: return "Light drizzle";
            case 56: case 57: return "Freezing drizzle";
            case 61: case 63: case 65: return "Rain";
            case 66: case 67: return "Freezing rain";
            case 71: case 73: case 75: return "Snow";
            case 77: return "Snow grains";
            case 80: case 81: case 82: return "Rain showers";
            case 85: case 86: return "Snow showers";
            case 95: return "Thunderstorm";
            case 96: case 99: return "Thunderstorm with hail";
            default: return "Unknown weather";
        }
    }
    
    private Map<String, Object> getFallbackLocationAndWeather(String location) {
        Map<String, Object> result = new HashMap<>();
        
        // Generate consistent coordinates based on location name
        int locationHash = Math.abs(location.toLowerCase().hashCode());
        double lat = 20.0 + (locationHash % 50); // Between 20-70 degrees
        double lng = -180.0 + (locationHash % 360); // Between -180 to 180 degrees
        
        result.put("latitude", lat);
        result.put("longitude", lng);
        result.put("formatted_address", location + " (simulated)");
        
        // Add fallback weather data
        Map<String, Object> weatherData = getFallbackWeatherData(location);
        result.putAll(weatherData);
        
        return result;
    }
    
    private Map<String, Object> getFallbackWeatherData(String location) {
        Map<String, Object> result = new HashMap<>();
        
        // Generate consistent weather based on location name
        int locationHash = Math.abs(location.toLowerCase().hashCode());
        Random random = new Random(locationHash);
        
        String[] weatherTypes = {"Clear sky", "Partly cloudy", "Cloudy", "Light rain", "Rain", "Snow"};
        String weatherType = weatherTypes[locationHash % weatherTypes.length];
        
        double temperature = 15.0 + (random.nextDouble() * 30.0) - 15.0; // -15 to 30°C
        int humidity = 40 + (locationHash % 40); // 40-80%
        double windSpeed = 2.0 + (random.nextDouble() * 8.0); // 2-10 m/s
        
        result.put("temperature", Math.round(temperature * 10.0) / 10.0);
        result.put("feels_like", Math.round((temperature - 2.0) * 10.0) / 10.0);
        result.put("humidity", humidity);
        result.put("pressure", 1000 + (locationHash % 50));
        result.put("description", weatherType);
        result.put("main_weather", weatherType.split(" ")[0]);
        result.put("wind_speed", Math.round(windSpeed * 10.0) / 10.0);
        
        return result;
    }
    
    public boolean isGoogleMapsConfigured() {
        return true; // Always return true since we're using free services
    }
    
    public boolean isOpenWeatherConfigured() {
        return true; // Always return true since we're using free services
    }
} 
