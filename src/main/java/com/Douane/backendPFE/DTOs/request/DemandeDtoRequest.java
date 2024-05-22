package com.Douane.backendPFE.DTOs.request;

import com.Douane.backendPFE.models.demandeAutorisation.enums.*;
import com.Douane.backendPFE.models.vehiculle.Marque;
import lombok.Data;

@Data
public class DemandeDtoRequest {
    private String typeDemande;

    //Num cin ou passpor
    private Long codeDemande;
    private Long numDiptyque;


    private String expirationAt;
    private BrRattachement brRattachement;
    private BrFrontalier brFrontalier;



    private String info;
    private Pays paysOrigine;
    private  Pays nationalite;
    private String motif;

    private String autorisation;
    private Long idBureauAutorite;

    private String numchassis;
    private String numImmatriculation;

    private Marque marque;
    private String complementMarque;


    /* modeDapurement */

    private Double quantiteQCS;
    private Double poidsNet;
    private Double valeurDeviseEtrangere;
    private Double valeurDT;

    /* modeDapurement */

    private String motifArret;
    private String description;

    private String interdictionsEtRestrictions;
    private String guaranteeReferenceNumber;

    /*Propriteure*/

    private String nom;
    private String prenom;
    private String cinCarteSejour;

    /*Propriteure*/
    private float montantGarantie;

    private String declartionPrecedente;
    private String echianceDate;

}






