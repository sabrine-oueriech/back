package com.Douane.backendPFE.models.vehiculle;

import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehiculle")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehiculle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numchassis;
    private String numImmatriculation;
    @Enumerated(EnumType.STRING)
    private Marque marque;
    private String complementMarque;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime updateAt;

    @OneToMany( fetch = FetchType.LAZY ,cascade=CascadeType.REMOVE)
    private Set<Demande> demandesAuto=new HashSet<>();

}
