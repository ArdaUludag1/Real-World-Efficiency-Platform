package com.yourorg.routedashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteRequest {
    @NotBlank
    private String make;
    
    @NotNull
    private Integer year;
    
    @NotBlank
    private String model;
    
    @NotBlank
    private String fromCity;
    
    @NotBlank
    private String toCity;
    
    // Explicit getters and setters
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getFromCity() { return fromCity; }
    public void setFromCity(String fromCity) { this.fromCity = fromCity; }
    
    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }
} 