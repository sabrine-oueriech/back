package com.Douane.backendPFE.services;

import com.Douane.backendPFE.models.contact.MessageContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<MessageContact, Long> {
    // Vous pouvez ajouter des méthodes personnalisées ici si nécessaire
}


