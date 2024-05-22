package com.Douane.backendPFE.models.contact;

import com.Douane.backendPFE.models.bureau.Bureau;

import com.Douane.backendPFE.models.user.UserModel;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;



@Builder

@Entity

@Data

@AllArgsConstructor

@NoArgsConstructor

@Table(name = "contact")

public class MessageContact {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String subject;

    private String email;

    private String name;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private UserModel usersender;

    private boolean archiveAuto = false;






}
