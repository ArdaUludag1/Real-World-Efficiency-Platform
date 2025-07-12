package com.yourorg.routedashboard.repository;

import com.yourorg.routedashboard.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    // Find all trips by user ID
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find the most recent trip by user ID
    @Query("SELECT t FROM Trip t WHERE t.userId = :userId ORDER BY t.createdAt DESC")
    Optional<Trip> findMostRecentByUserId(@Param("userId") Long userId);
    
    // Find trips by user ID with pagination
    @Query("SELECT t FROM Trip t WHERE t.userId = :userId ORDER BY t.createdAt DESC")
    List<Trip> findByUserIdWithPagination(@Param("userId") Long userId);
    
    // Count trips by user ID
    long countByUserId(Long userId);
} 