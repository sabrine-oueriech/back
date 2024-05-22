package com.Douane.backendPFE.controllers;

import com.Douane.backendPFE.DTOs.request.ContactDto;
import com.Douane.backendPFE.models.contact.MessageContact;
import com.Douane.backendPFE.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin()
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ContactDto contactDto) {
        try {
            MessageContact message = contactService.saveMessage(contactDto);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send message: " + e.getMessage());
        }
    }
    @GetMapping("/messages")
    public ResponseEntity<List<MessageContact>> getAllMessages() {
        try {
            List<MessageContact> messages = contactService.getAllMessages();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

