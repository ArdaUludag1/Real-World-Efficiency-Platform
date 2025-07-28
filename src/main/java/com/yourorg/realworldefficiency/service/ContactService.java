package com.yourorg.realworldefficiency.service;

import com.yourorg.realworldefficiency.dto.ContactForm;
import com.yourorg.realworldefficiency.entity.ContactMessage;
import com.yourorg.realworldefficiency.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for handling Contact Us form submissions.
 */
@Service
public class ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Saves a contact form submission.
     * @param form the contact form DTO
     */
    public void saveContactForm(ContactForm form) {
        ContactMessage message = new ContactMessage();
        message.setName(form.getName());
        message.setEmail(form.getEmail());
        message.setMessage(form.getMessage());
        contactRepository.save(message);
    }
} 
