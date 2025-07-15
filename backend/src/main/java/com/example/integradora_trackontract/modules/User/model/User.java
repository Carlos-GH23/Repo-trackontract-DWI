package com.example.integradora_trackontract.modules.User.model;

import com.example.integradora_trackontract.auth.repository.Token;
import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_Logs;
import com.example.integradora_trackontract.modules.Password_Resets.model.Password_Resets;
import com.example.integradora_trackontract.modules.Roles.model.Roles;
import com.example.integradora_trackontract.modules.User_Contracts.model.User_Contracts;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = false)
    private String name;

    @Column(name = "last_name", columnDefinition = "VARCHAR(50)")
    private String lastName;

    @Column(name = "email", columnDefinition = "VARCHAR(100)", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", columnDefinition = "VARCHAR(15)", nullable = false)
    private String phone;

    @Column(name = "password", columnDefinition = "VARCHAR(255)", nullable = false)
    private String password;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String updated_at;

    @Column(name = "login_attempts", columnDefinition = "INT DEFAULT 0")
    private int login_attempts;



    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    private List<Audit_Logs> audit_logs;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    private List<Password_Resets> password_resets;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    private List<User_Contracts> user_contracts;

    @ManyToOne
    @JsonIgnore
    private Roles rol_id;
}
