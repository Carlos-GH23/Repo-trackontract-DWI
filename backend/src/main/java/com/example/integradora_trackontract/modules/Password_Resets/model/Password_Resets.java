package com.example.integradora_trackontract.modules.Password_Resets.model;

import com.example.integradora_trackontract.modules.User.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "password_resets")
public class Password_Resets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", columnDefinition = "VARCHAR(255)", nullable = false)
    private String token;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private String created_at;

    @Column(name = "used_at", columnDefinition = "TIMESTAMP NULL")
    private String used_at;

    @ManyToOne
    @JsonIgnore
    private User user_id;
}
