package com.yourorg.routedashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles", uniqueConstraints = @UniqueConstraint(columnNames = {"make", "model", "year", "trim"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String make;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 100)
    private String trim;

    @Column(name = "fuel_consumption_city")
    private Double fuelConsumptionCity;

    @Column(name = "fuel_consumption_highway")
    private Double fuelConsumptionHighway;

    @Column(name = "fuel_consumption_mixed")
    private Double fuelConsumptionMixed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Explicit getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getTrim() { return trim; }
    public void setTrim(String trim) { this.trim = trim; }
    public Double getFuelConsumptionCity() { return fuelConsumptionCity; }
    public void setFuelConsumptionCity(Double fuelConsumptionCity) { this.fuelConsumptionCity = fuelConsumptionCity; }
    public Double getFuelConsumptionHighway() { return fuelConsumptionHighway; }
    public void setFuelConsumptionHighway(Double fuelConsumptionHighway) { this.fuelConsumptionHighway = fuelConsumptionHighway; }
    public Double getFuelConsumptionMixed() { return fuelConsumptionMixed; }
    public void setFuelConsumptionMixed(Double fuelConsumptionMixed) { this.fuelConsumptionMixed = fuelConsumptionMixed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Builder pattern
    public static VehicleBuilder builder() {
        return new VehicleBuilder();
    }
    
    public static class VehicleBuilder {
        private Long id;
        private String make;
        private String model;
        private Integer year;
        private LocalDateTime createdAt;
        private String trim;
        private Double fuelConsumptionCity;
        private Double fuelConsumptionHighway;
        private Double fuelConsumptionMixed;
        
        public VehicleBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public VehicleBuilder make(String make) {
            this.make = make;
            return this;
        }
        
        public VehicleBuilder model(String model) {
            this.model = model;
            return this;
        }
        
        public VehicleBuilder year(Integer year) {
            this.year = year;
            return this;
        }
        
        public VehicleBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public VehicleBuilder trim(String trim) {
            this.trim = trim;
            return this;
        }
        public VehicleBuilder fuelConsumptionCity(Double fuelConsumptionCity) {
            this.fuelConsumptionCity = fuelConsumptionCity;
            return this;
        }
        public VehicleBuilder fuelConsumptionHighway(Double fuelConsumptionHighway) {
            this.fuelConsumptionHighway = fuelConsumptionHighway;
            return this;
        }
        public VehicleBuilder fuelConsumptionMixed(Double fuelConsumptionMixed) {
            this.fuelConsumptionMixed = fuelConsumptionMixed;
            return this;
        }
        
        public Vehicle build() {
            Vehicle vehicle = new Vehicle();
            vehicle.id = this.id;
            vehicle.make = this.make;
            vehicle.model = this.model;
            vehicle.year = this.year;
            vehicle.createdAt = this.createdAt;
            vehicle.trim = this.trim;
            vehicle.fuelConsumptionCity = this.fuelConsumptionCity;
            vehicle.fuelConsumptionHighway = this.fuelConsumptionHighway;
            vehicle.fuelConsumptionMixed = this.fuelConsumptionMixed;
            return vehicle;
        }
    }
} 