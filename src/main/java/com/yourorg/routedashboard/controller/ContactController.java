package com.yourorg.routedashboard.controller;

import com.yourorg.routedashboard.dto.ContactForm;
import com.yourorg.routedashboard.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for Contact Us form submissions.
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Handles POST /api/contact/submit for contact form submissions.
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitContactForm(@Valid @RequestBody ContactForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Return first validation error message
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMsg);
        }
        contactService.saveContactForm(form);
        return ResponseEntity.ok("Thank you for contacting us! We have received your message.");
    }
} 