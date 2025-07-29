package com.example.integradora_trackontract.modules.Roles.model;

import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User_Contracts.model.User_Contracts;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(30)", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "VARCHAR(100)", nullable = true)
    private String description;

    @OneToMany(mappedBy = "rol_id", cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "role_id", cascade = CascadeType.ALL)
    private List<User_Contracts> userContracts;


}
