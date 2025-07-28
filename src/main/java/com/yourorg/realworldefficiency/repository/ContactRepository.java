package com.yourorg.realworldefficiency.repository;

import com.yourorg.realworldefficiency.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ContactMessage entity.
 */
@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
} 
