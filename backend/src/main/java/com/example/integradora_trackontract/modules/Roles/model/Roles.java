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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<User_Contracts> getUserContracts() {
        return userContracts;
    }

    public void setUserContracts(List<User_Contracts> userContracts) {
        this.userContracts = userContracts;
    }
}
