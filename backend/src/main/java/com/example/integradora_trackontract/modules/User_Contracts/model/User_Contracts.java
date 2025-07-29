package com.example.integradora_trackontract.modules.User_Contracts.model;

import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.Roles.model.Roles;
import com.example.integradora_trackontract.modules.User.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "user_contracts")
public class User_Contracts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assigned_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String assigned_at;

    @ManyToOne
    @JsonIgnore
    private Contracts contract_id;

    @ManyToOne
    @JsonIgnore
    private Roles role_id;

    @ManyToOne
    @JsonIgnore
    private User user_id;
}
