package com.Douane.backendPFE.models.bureau;

import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.Douane.backendPFE.models.user.Role;
import com.Douane.backendPFE.models.user.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bureau")
public class Bureau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Set<ETache> taches;
    @OneToMany (mappedBy = "bureau" ,fetch = FetchType.EAGER ,cascade=CascadeType.ALL)
    private Set<UserModel> users=new HashSet<>();


}
