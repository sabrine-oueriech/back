package com.Douane.backendPFE.services;

import com.Douane.backendPFE.DTOs.request.ContactDto;
import com.Douane.backendPFE.DTOs.response.UserDto;
import com.Douane.backendPFE.models.contact.MessageContact;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.services.userS.UserService;
import com.Douane.backendPFE.services.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;  // Injection du UserService

    public List<MessageContact> getAllMessages() {
        return contactRepository.findAll();
    }

    public MessageContact saveMessage(ContactDto contactDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserModel user = userService.findByEmail(email);

        // Créer et sauvegarder le message de contact
        MessageContact message = MessageContact.builder()
                .subject(contactDto.getSubject())
                .email(user.getEmail())  // Email de l'utilisateur
                .name(user.getFirstname() + " " + user.getLastname())  // Nom complet de l'utilisateur
                .message(contactDto.getMessage())

                .build();

        return contactRepository.save(message);
    }
}