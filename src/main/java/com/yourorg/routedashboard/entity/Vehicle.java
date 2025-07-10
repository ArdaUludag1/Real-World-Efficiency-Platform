package com.yourorg.routedashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
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
        
        public Vehicle build() {
            Vehicle vehicle = new Vehicle();
            vehicle.id = this.id;
            vehicle.make = this.make;
            vehicle.model = this.model;
            vehicle.year = this.year;
            vehicle.createdAt = this.createdAt;
            return vehicle;
        }
    }
} 