package com.yourorg.realworldefficiency.repository;

import com.yourorg.realworldefficiency.entity.History;
import com.yourorg.realworldefficiency.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    void deleteByUser(com.yourorg.realworldefficiency.entity.User user);
    List<History> findByTripId(Long tripId);
    List<History> findByUser(User user);
} 
