package com.yourorg.routedashboard.repository;

import com.yourorg.routedashboard.entity.History;
import com.yourorg.routedashboard.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
} 