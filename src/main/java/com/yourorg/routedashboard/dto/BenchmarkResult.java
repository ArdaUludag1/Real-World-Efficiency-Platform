package com.yourorg.routedashboard.dto;

public class BenchmarkResult {
    
    private String make;
    private String model;
    private Integer year;
    private String fromCity;
    private String toCity;
    private Double distanceKm;
    private Double fuelConsumptionActual;
    private Double fuelConsumptionBaseline;
    private Double differenceLiters;
    private Double percentageChange;
    private String efficiencyStatus; // "Efficient", "Average", "Inefficient"
    
    // Default constructor
    public BenchmarkResult() {}
    
    // Constructor with all fields
    public BenchmarkResult(String make, String model, Integer year, String fromCity, String toCity,
                          Double distanceKm, Double fuelConsumptionActual, Double fuelConsumptionBaseline) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.distanceKm = distanceKm;
        this.fuelConsumptionActual = fuelConsumptionActual;
        this.fuelConsumptionBaseline = fuelConsumptionBaseline;
        
        // Calculate difference and percentage
        this.differenceLiters = fuelConsumptionActual - fuelConsumptionBaseline;
        this.percentageChange = (differenceLiters / fuelConsumptionBaseline) * 100;
        
        // Determine efficiency status
        if (percentageChange <= -10) {
            this.efficiencyStatus = "Efficient";
        } else if (percentageChange >= 10) {
            this.efficiencyStatus = "Inefficient";
        } else {
            this.efficiencyStatus = "Average";
        }
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
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public Double getFuelConsumptionActual() {
        return fuelConsumptionActual;
    }
    
    public void setFuelConsumptionActual(Double fuelConsumptionActual) {
        this.fuelConsumptionActual = fuelConsumptionActual;
    }
    
    public Double getFuelConsumptionBaseline() {
        return fuelConsumptionBaseline;
    }
    
    public void setFuelConsumptionBaseline(Double fuelConsumptionBaseline) {
        this.fuelConsumptionBaseline = fuelConsumptionBaseline;
    }
    
    public Double getDifferenceLiters() {
        return differenceLiters;
    }
    
    public void setDifferenceLiters(Double differenceLiters) {
        this.differenceLiters = differenceLiters;
    }
    
    public Double getPercentageChange() {
        return percentageChange;
    }
    
    public void setPercentageChange(Double percentageChange) {
        this.percentageChange = percentageChange;
    }
    
    public String getEfficiencyStatus() {
        return efficiencyStatus;
    }
    
    public void setEfficiencyStatus(String efficiencyStatus) {
        this.efficiencyStatus = efficiencyStatus;
    }
} 