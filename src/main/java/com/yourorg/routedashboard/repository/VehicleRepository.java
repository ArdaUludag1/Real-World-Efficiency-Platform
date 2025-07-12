package com.yourorg.routedashboard.repository;

import com.yourorg.routedashboard.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    @Query("SELECT DISTINCT v.make FROM Vehicle v ORDER BY v.make")
    List<String> findAllMakes();
    
    @Query("SELECT DISTINCT v.year FROM Vehicle v WHERE v.make = :make ORDER BY v.year DESC")
    List<Integer> findYearsByMake(@Param("make") String make);
    
    @Query("SELECT DISTINCT v.model FROM Vehicle v WHERE v.make = :make AND v.year = :year ORDER BY v.model")
    List<String> findModelsByMakeAndYear(@Param("make") String make, @Param("year") Integer year);
    
    Vehicle findByMakeAndModelAndYear(String make, String model, Integer year);
    
    List<Vehicle> findByMake(String make);
    
    List<Vehicle> findByMakeAndYear(String make, Integer year);
    
    // Method to clear all vehicles
    void deleteAll();
} 