package com.Douane.backendPFE.models.user;

import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private ERole name;



}
