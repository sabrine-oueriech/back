package com.Douane.backendPFE.models.demandeAutorisation;

import com.Douane.backendPFE.models.autorisation.Autorisation;
import com.Douane.backendPFE.models.bureau.Bureau;
import com.Douane.backendPFE.models.demandeAutorisation.enums.*;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.models.vehiculle.Marque;
import com.Douane.backendPFE.models.vehiculle.Vehiculle;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "demande")
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeDemande type;
    private String NumRef;
    @Embedded
    private DocumentMetadata documentMetadata=new DocumentMetadata();
    //Nom_Prenome de demandeur
    private String desiDemande;
    //Num cin ou passpor
    private Long codeDemande;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime expirationAt;
    @Enumerated(EnumType.STRING)
    private BrRattachement brRattachement;
    @Enumerated(EnumType.STRING)
    private BrFrontalier brFrontalier;
    //verification de champ de vehiculle
    private Boolean idfMarchandises;
    @Enumerated(EnumType.STRING)
    private Set<EtatReserve> etatReserve;
    private String info;
    @Enumerated(EnumType.STRING)
    private Pays paysOrigine;
    @Enumerated(EnumType.STRING)
    private  Pays nationalite;
    @Enumerated(EnumType.STRING)
    private Motif motif;
    //12 alfabitque et num
    private String numDiptyque;
    @Enumerated(EnumType.STRING)
    private Autorisation autorisation;

    @Enumerated(EnumType.STRING)
    private Statue statueDem;
    //verification de vehiculle
    private String numchassis;
    private String numImmatriculation;
    private Marque marque;
   /* debut  champs des autre autrisation */


    //autorisation 300 400
    @Enumerated(EnumType.STRING)
    private MotifArret motifArret;
    // Autorisation 200 300 400
    @Embedded
    private ModeDapurement modeDapurement ;
    // Autoritation 400
    private String description;
    // Autorisation 200 300 400
    @Enumerated(EnumType.STRING)
    private InterdictionsEtRestrictions interdictionsEtRestrictions;
    //piece jointe et num de reference Autorisation 300
    private String guaranteeReferenceNumber;
    //montant garantie Autorisation 300   apre  lajoutele piece de grant
    private float montantGarantie;

    // Alternatively, store the file location if using file storage
    private String guaranteeDocumentUrl;
    //Autorisation 400
    @Embedded
    private Proprietaire proprietaire;
    //mzl declaration precedente w echiance de paiement
    // declaration precedente hya input aadya w echiance date
    //Autoritaion 200 300 400
    private String declartionPrecedente;
    //Autrisation 200 date hetha mta3 chnia anhou payemant payement mta3 declaration precedente 5tr homa i5also b tranche
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime echianceDate;

   /* end  champs des autre autrisation */

    private boolean archiveAuto;

    /* relason entre les table demande */
    @ManyToOne(fetch = FetchType.LAZY ,cascade=CascadeType.DETACH)
    @JoinColumn(name = "Bureau_id")
    private Bureau bureauAutorite;

    @ManyToOne(fetch = FetchType.LAZY ,cascade=CascadeType.DETACH)
    @JoinColumn(name = "Vehiculle_id")
    private Vehiculle vehiculle;


    @ManyToOne(fetch = FetchType.LAZY ,cascade=CascadeType.DETACH)
    @JoinColumn(name = "create_user_id")
    private UserModel createUser;

    @ManyToOne(fetch = FetchType.LAZY ,cascade=CascadeType.DETACH)
    @JoinColumn(name = "verify_user_id")
    private UserModel verifyUser;

    @ManyToOne(fetch = FetchType.LAZY ,cascade=CascadeType.DETACH)
    @JoinColumn(name = "validate_user_id")
    private UserModel validateUser;




    @PostPersist
    public void init() {
        documentMetadata = new DocumentMetadata();
        proprietaire = new Proprietaire();
        modeDapurement = new ModeDapurement();
    }
}
