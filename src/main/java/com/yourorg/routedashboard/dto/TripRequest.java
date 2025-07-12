package com.yourorg.routedashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class TripRequest {
    
    @NotBlank(message = "Make is required")
    private String make;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotNull(message = "Year is required")
    @Min(value = 1994, message = "Year must be at least 1994")
    @Max(value = 2024, message = "Year must be at most 2024")
    private Integer year;
    
    @NotBlank(message = "From city is required")
    private String fromCity;
    
    @NotBlank(message = "To city is required")
    private String toCity;
    
    @NotNull(message = "Fuel consumption is required")
    @Min(value = 1, message = "Fuel consumption must be at least 1 L/100km")
    @Max(value = 50, message = "Fuel consumption must be at most 50 L/100km")
    private Double fuelConsumptionActual;
    
    // Default constructor
    public TripRequest() {}
    
    // Constructor with all fields
    public TripRequest(String make, String model, Integer year, String fromCity, String toCity, Double fuelConsumptionActual) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.fuelConsumptionActual = fuelConsumptionActual;
    }
    
    // Getters and Setters
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public String getFromCity() {
        return fromCity;
    }
    
    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }
    
    public String getToCity() {
        return toCity;
    }
    
    public void setToCity(String toCity) {
        this.toCity = toCity;
    }
    
    public Double getFuelConsumptionActual() {
        return fuelConsumptionActual;
    }
    
    public void setFuelConsumptionActual(Double fuelConsumptionActual) {
        this.fuelConsumptionActual = fuelConsumptionActual;
    }
} 