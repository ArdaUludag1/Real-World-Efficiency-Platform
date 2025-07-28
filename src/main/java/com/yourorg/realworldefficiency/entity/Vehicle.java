package com.yourorg.realworldefficiency.entity;

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

    @Column(name = "model_id")
    private String modelId;

    @Column(name = "model_engine_position")
    private String modelEnginePosition;

    @Column(name = "model_engine_cc")
    private Integer modelEngineCc;

    @Column(name = "model_engine_cyl")
    private Integer modelEngineCyl;

    @Column(name = "model_engine_type")
    private String modelEngineType;

    @Column(name = "model_engine_valves_per_cyl")
    private Integer modelEngineValvesPerCyl;

    @Column(name = "model_engine_power_ps")
    private Integer modelEnginePowerPs;

    @Column(name = "model_engine_power_rpm")
    private Integer modelEnginePowerRpm;

    @Column(name = "model_engine_torque_nm")
    private Integer modelEngineTorqueNm;

    @Column(name = "model_engine_torque_rpm")
    private Integer modelEngineTorqueRpm;

    @Column(name = "model_engine_bore_mm")
    private Double modelEngineBoreMm;

    @Column(name = "model_engine_stroke_mm")
    private Double modelEngineStrokeMm;

    @Column(name = "model_engine_compression")
    private String modelEngineCompression;

    @Column(name = "model_engine_fuel")
    private String modelEngineFuel;

    @Column(name = "model_top_speed_kph")
    private Integer modelTopSpeedKph;

    @Column(name = "model_0_to_100_kph")
    private Double model0To100Kph;

    @Column(name = "model_drive")
    private String modelDrive;

    @Column(name = "model_transmission_type")
    private String modelTransmissionType;

    @Column(name = "model_seats")
    private Integer modelSeats;

    @Column(name = "model_doors")
    private Integer modelDoors;

    @Column(name = "model_weight_kg")
    private Integer modelWeightKg;

    @Column(name = "model_length_mm")
    private Integer modelLengthMm;

    @Column(name = "model_width_mm")
    private Integer modelWidthMm;

    @Column(name = "model_height_mm")
    private Integer modelHeightMm;

    @Column(name = "model_wheelbase_mm")
    private Integer modelWheelbaseMm;

    @Column(name = "model_lkm_hwy")
    private Double modelLkmHwy;

    @Column(name = "model_lkm_mixed")
    private Double modelLkmMixed;

    @Column(name = "model_lkm_city")
    private Double modelLkmCity;

    @Column(name = "model_fuel_cap_l")
    private Double modelFuelCapL;

    @Column(name = "model_sold_in_us")
    private Boolean modelSoldInUs;

    @Column(name = "model_co2")
    private Double modelCo2;

    @Column(name = "model_make_display")
    private String modelMakeDisplay;

    @Column(name = "make_display")
    private String makeDisplay;

    @Column(name = "make_country")
    private String makeCountry;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

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
    
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public String getModelEnginePosition() { return modelEnginePosition; }
    public void setModelEnginePosition(String modelEnginePosition) { this.modelEnginePosition = modelEnginePosition; }

    public Integer getModelEngineCc() { return modelEngineCc; }
    public void setModelEngineCc(Integer modelEngineCc) { this.modelEngineCc = modelEngineCc; }

    public Integer getModelEngineCyl() { return modelEngineCyl; }
    public void setModelEngineCyl(Integer modelEngineCyl) { this.modelEngineCyl = modelEngineCyl; }

    public String getModelEngineType() { return modelEngineType; }
    public void setModelEngineType(String modelEngineType) { this.modelEngineType = modelEngineType; }

    public Integer getModelEngineValvesPerCyl() { return modelEngineValvesPerCyl; }
    public void setModelEngineValvesPerCyl(Integer modelEngineValvesPerCyl) { this.modelEngineValvesPerCyl = modelEngineValvesPerCyl; }

    public Integer getModelEnginePowerPs() { return modelEnginePowerPs; }
    public void setModelEnginePowerPs(Integer modelEnginePowerPs) { this.modelEnginePowerPs = modelEnginePowerPs; }

    public Integer getModelEnginePowerRpm() { return modelEnginePowerRpm; }
    public void setModelEnginePowerRpm(Integer modelEnginePowerRpm) { this.modelEnginePowerRpm = modelEnginePowerRpm; }

    public Integer getModelEngineTorqueNm() { return modelEngineTorqueNm; }
    public void setModelEngineTorqueNm(Integer modelEngineTorqueNm) { this.modelEngineTorqueNm = modelEngineTorqueNm; }

    public Integer getModelEngineTorqueRpm() { return modelEngineTorqueRpm; }
    public void setModelEngineTorqueRpm(Integer modelEngineTorqueRpm) { this.modelEngineTorqueRpm = modelEngineTorqueRpm; }

    public Double getModelEngineBoreMm() { return modelEngineBoreMm; }
    public void setModelEngineBoreMm(Double modelEngineBoreMm) { this.modelEngineBoreMm = modelEngineBoreMm; }

    public Double getModelEngineStrokeMm() { return modelEngineStrokeMm; }
    public void setModelEngineStrokeMm(Double modelEngineStrokeMm) { this.modelEngineStrokeMm = modelEngineStrokeMm; }

    public String getModelEngineCompression() { return modelEngineCompression; }
    public void setModelEngineCompression(String modelEngineCompression) { this.modelEngineCompression = modelEngineCompression; }

    public String getModelEngineFuel() { return modelEngineFuel; }
    public void setModelEngineFuel(String modelEngineFuel) { this.modelEngineFuel = modelEngineFuel; }

    public Integer getModelTopSpeedKph() { return modelTopSpeedKph; }
    public void setModelTopSpeedKph(Integer modelTopSpeedKph) { this.modelTopSpeedKph = modelTopSpeedKph; }

    public Double getModel0To100Kph() { return model0To100Kph; }
    public void setModel0To100Kph(Double model0To100Kph) { this.model0To100Kph = model0To100Kph; }

    public String getModelDrive() { return modelDrive; }
    public void setModelDrive(String modelDrive) { this.modelDrive = modelDrive; }

    public String getModelTransmissionType() { return modelTransmissionType; }
    public void setModelTransmissionType(String modelTransmissionType) { this.modelTransmissionType = modelTransmissionType; }

    public Integer getModelSeats() { return modelSeats; }
    public void setModelSeats(Integer modelSeats) { this.modelSeats = modelSeats; }

    public Integer getModelDoors() { return modelDoors; }
    public void setModelDoors(Integer modelDoors) { this.modelDoors = modelDoors; }

    public Integer getModelWeightKg() { return modelWeightKg; }
    public void setModelWeightKg(Integer modelWeightKg) { this.modelWeightKg = modelWeightKg; }

    public Integer getModelLengthMm() { return modelLengthMm; }
    public void setModelLengthMm(Integer modelLengthMm) { this.modelLengthMm = modelLengthMm; }

    public Integer getModelWidthMm() { return modelWidthMm; }
    public void setModelWidthMm(Integer modelWidthMm) { this.modelWidthMm = modelWidthMm; }

    public Integer getModelHeightMm() { return modelHeightMm; }
    public void setModelHeightMm(Integer modelHeightMm) { this.modelHeightMm = modelHeightMm; }

    public Integer getModelWheelbaseMm() { return modelWheelbaseMm; }
    public void setModelWheelbaseMm(Integer modelWheelbaseMm) { this.modelWheelbaseMm = modelWheelbaseMm; }

    public Double getModelLkmHwy() { return modelLkmHwy; }
    public void setModelLkmHwy(Double modelLkmHwy) { this.modelLkmHwy = modelLkmHwy; }

    public Double getModelLkmMixed() { return modelLkmMixed; }
    public void setModelLkmMixed(Double modelLkmMixed) { this.modelLkmMixed = modelLkmMixed; }

    public Double getModelLkmCity() { return modelLkmCity; }
    public void setModelLkmCity(Double modelLkmCity) { this.modelLkmCity = modelLkmCity; }

    public Double getModelFuelCapL() { return modelFuelCapL; }
    public void setModelFuelCapL(Double modelFuelCapL) { this.modelFuelCapL = modelFuelCapL; }

    public Boolean getModelSoldInUs() { return modelSoldInUs; }
    public void setModelSoldInUs(Boolean modelSoldInUs) { this.modelSoldInUs = modelSoldInUs; }

    public Double getModelCo2() { return modelCo2; }
    public void setModelCo2(Double modelCo2) { this.modelCo2 = modelCo2; }

    public String getModelMakeDisplay() { return modelMakeDisplay; }
    public void setModelMakeDisplay(String modelMakeDisplay) { this.modelMakeDisplay = modelMakeDisplay; }

    public String getMakeDisplay() { return makeDisplay; }
    public void setMakeDisplay(String makeDisplay) { this.makeDisplay = makeDisplay; }

    public String getMakeCountry() { return makeCountry; }
    public void setMakeCountry(String makeCountry) { this.makeCountry = makeCountry; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
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
        private LocalDateTime lastUpdated;
        
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

        public VehicleBuilder lastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
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
            vehicle.lastUpdated = this.lastUpdated;
            return vehicle;
        }
    }
} 
