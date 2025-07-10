package com.yourorg.routedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponse {
    private String make;
    private String model;
    private Integer year;
    private String fromCity;
    private String toCity;
    private Double distance;
    private Double baseConsumption;
    private Double adjustedConsumption;
    private Double difference;
    private Double differencePercentage;
    private String weatherFrom;
    private String weatherTo;
    
    // Explicit constructor
    public RouteResponse(String make, String model, Integer year, String fromCity, String toCity,
                       Double distance, Double baseConsumption, Double adjustedConsumption,
                       Double difference, Double differencePercentage, String weatherFrom, String weatherTo) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.distance = distance;
        this.baseConsumption = baseConsumption;
        this.adjustedConsumption = adjustedConsumption;
        this.difference = difference;
        this.differencePercentage = differencePercentage;
        this.weatherFrom = weatherFrom;
        this.weatherTo = weatherTo;
    }
    
    // Explicit getters and setters
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getFromCity() { return fromCity; }
    public void setFromCity(String fromCity) { this.fromCity = fromCity; }
    
    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public Double getBaseConsumption() { return baseConsumption; }
    public void setBaseConsumption(Double baseConsumption) { this.baseConsumption = baseConsumption; }
    
    public Double getAdjustedConsumption() { return adjustedConsumption; }
    public void setAdjustedConsumption(Double adjustedConsumption) { this.adjustedConsumption = adjustedConsumption; }
    
    public Double getDifference() { return difference; }
    public void setDifference(Double difference) { this.difference = difference; }
    
    public Double getDifferencePercentage() { return differencePercentage; }
    public void setDifferencePercentage(Double differencePercentage) { this.differencePercentage = differencePercentage; }
    
    public String getWeatherFrom() { return weatherFrom; }
    public void setWeatherFrom(String weatherFrom) { this.weatherFrom = weatherFrom; }
    
    public String getWeatherTo() { return weatherTo; }
    public void setWeatherTo(String weatherTo) { this.weatherTo = weatherTo; }
} 