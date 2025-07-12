package com.yourorg.routedashboard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip")
public class Trip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "make", nullable = false, length = 50)
    private String make;
    
    @Column(name = "model", nullable = false, length = 100)
    private String model;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Column(name = "from_city", nullable = false, length = 100)
    private String fromCity;
    
    @Column(name = "to_city", nullable = false, length = 100)
    private String toCity;
    
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;
    
    @Column(name = "fuel_consumption_actual", nullable = false)
    private Double fuelConsumptionActual;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Default constructor
    public Trip() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with all fields
    public Trip(Long userId, String make, String model, Integer year, 
                String fromCity, String toCity, Double distanceKm, Double fuelConsumptionActual) {
        this.userId = userId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.distanceKm = distanceKm;
        this.fuelConsumptionActual = fuelConsumptionActual;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 