package com.yourorg.routedashboard.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "from_city", nullable = false, length = 100)
    private String fromCity;

    @Column(name = "to_city", nullable = false, length = 100)
    private String toCity;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(name = "base_consumption_l", nullable = false)
    private Double baseConsumptionL;

    @Column(name = "adjusted_consumption_l", nullable = false)
    private Double adjustedConsumptionL;

    @Column(name = "weather_from", length = 255)
    private String weatherFrom;

    @Column(name = "weather_to", length = 255)
    private String weatherTo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Explicit getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    
    public String getFromCity() { return fromCity; }
    public void setFromCity(String fromCity) { this.fromCity = fromCity; }
    
    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }
    
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    
    public Double getBaseConsumptionL() { return baseConsumptionL; }
    public void setBaseConsumptionL(Double baseConsumptionL) { this.baseConsumptionL = baseConsumptionL; }
    
    public Double getAdjustedConsumptionL() { return adjustedConsumptionL; }
    public void setAdjustedConsumptionL(Double adjustedConsumptionL) { this.adjustedConsumptionL = adjustedConsumptionL; }
    
    public String getWeatherFrom() { return weatherFrom; }
    public void setWeatherFrom(String weatherFrom) { this.weatherFrom = weatherFrom; }
    
    public String getWeatherTo() { return weatherTo; }
    public void setWeatherTo(String weatherTo) { this.weatherTo = weatherTo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Builder pattern
    public static HistoryBuilder builder() {
        return new HistoryBuilder();
    }
    
    public static class HistoryBuilder {
        private Long id;
        private User user;
        private Vehicle vehicle;
        private String fromCity;
        private String toCity;
        private Double distanceKm;
        private Double baseConsumptionL;
        private Double adjustedConsumptionL;
        private String weatherFrom;
        private String weatherTo;
        private LocalDateTime createdAt;
        
        public HistoryBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public HistoryBuilder user(User user) {
            this.user = user;
            return this;
        }
        
        public HistoryBuilder vehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
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
        
        public HistoryBuilder baseConsumptionL(Double baseConsumptionL) {
            this.baseConsumptionL = baseConsumptionL;
            return this;
        }
        
        public HistoryBuilder adjustedConsumptionL(Double adjustedConsumptionL) {
            this.adjustedConsumptionL = adjustedConsumptionL;
            return this;
        }
        
        public HistoryBuilder weatherFrom(String weatherFrom) {
            this.weatherFrom = weatherFrom;
            return this;
        }
        
        public HistoryBuilder weatherTo(String weatherTo) {
            this.weatherTo = weatherTo;
            return this;
        }
        
        public HistoryBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public History build() {
            History history = new History();
            history.id = this.id;
            history.user = this.user;
            history.vehicle = this.vehicle;
            history.fromCity = this.fromCity;
            history.toCity = this.toCity;
            history.distanceKm = this.distanceKm;
            history.baseConsumptionL = this.baseConsumptionL;
            history.adjustedConsumptionL = this.adjustedConsumptionL;
            history.weatherFrom = this.weatherFrom;
            history.weatherTo = this.weatherTo;
            history.createdAt = this.createdAt;
            return history;
        }
    }
} 