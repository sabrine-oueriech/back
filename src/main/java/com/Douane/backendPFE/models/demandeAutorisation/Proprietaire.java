package com.Douane.backendPFE.models.demandeAutorisation;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Proprietaire {
    private String nom;
    private String prenom;
    private String cinCarteSejour;
}