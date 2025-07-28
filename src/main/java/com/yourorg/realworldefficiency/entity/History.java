package com.yourorg.realworldefficiency.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id")
    private Long tripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Explicit getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
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
    
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    
    public Double getFuelConsumptionActual() { return fuelConsumptionActual; }
    public void setFuelConsumptionActual(Double fuelConsumptionActual) { this.fuelConsumptionActual = fuelConsumptionActual; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Builder pattern
    public static HistoryBuilder builder() {
        return new HistoryBuilder();
    }
    
    public static class HistoryBuilder {
        private Long id;
        private Long tripId;
        private User user;
        private String make;
        private String model;
        private Integer year;
        private String fromCity;
        private String toCity;
        private Double distanceKm;
        private Double fuelConsumptionActual;
        private LocalDateTime createdAt;
        
        public HistoryBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public HistoryBuilder tripId(Long tripId) {
            this.tripId = tripId;
            return this;
        }
        
        public HistoryBuilder user(User user) {
            this.user = user;
            return this;
        }
        
        public HistoryBuilder make(String make) {
            this.make = make;
            return this;
        }
        
        public HistoryBuilder model(String model) {
            this.model = model;
            return this;
        }
        
        public HistoryBuilder year(Integer year) {
            this.year = year;
            return this;
        }
        
        public HistoryBuilder fromCity(String fromCity) {
            this.fromCity = fromCity;
            return this;
        }
        
        public HistoryBuilder toCity(String toCity) {
            this.toCity = toCity;
            return this;
        }
        
        public HistoryBuilder distanceKm(Double distanceKm) {
            this.distanceKm = distanceKm;
            return this;
        }
        
        public HistoryBuilder fuelConsumptionActual(Double fuelConsumptionActual) {
            this.fuelConsumptionActual = fuelConsumptionActual;
            return this;
        }
        
        public HistoryBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public History build() {
            History history = new History();
            history.id = this.id;
            history.tripId = this.tripId;
            history.user = this.user;
            history.make = this.make;
            history.model = this.model;
            history.year = this.year;
            history.fromCity = this.fromCity;
            history.toCity = this.toCity;
            history.distanceKm = this.distanceKm;
            history.fuelConsumptionActual = this.fuelConsumptionActual;
            history.createdAt = this.createdAt;
            return history;
        }
    }
} 
