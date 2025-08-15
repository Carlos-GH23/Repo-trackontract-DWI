package com.example.integradora_trackontract.modules.User.model;

import com.example.integradora_trackontract.auth.repository.Token;
import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_Logs;
import com.example.integradora_trackontract.modules.Password_Resets.model.Password_Resets;
import com.example.integradora_trackontract.modules.Roles.model.Roles;
import com.example.integradora_trackontract.modules.User_Contracts.model.User_Contracts;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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

    @Column(name = "phoneNumber", columnDefinition = "VARCHAR(15)", nullable = false)
    private String phoneNumber;

    @Column(name = "password", columnDefinition = "VARCHAR(255)", nullable = false)
    private String password;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime  created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updated_at;

    @Column(name = "login_attempts", columnDefinition = "INT DEFAULT 0")
    private int login_attempts;

    @Column(name = "locked_until")
    private LocalDateTime locked_until;

    public User() {
    }

    public User(Long id, String name, String lastName, String email, String phoneNumber, String password, boolean status, LocalDateTime created_at, LocalDateTime updated_at, int login_attempts, LocalDateTime locked_until, List<Token> tokens, List<Audit_Logs> audit_logs, List<Password_Resets> password_resets, List<User_Contracts> user_contracts, Roles rol_id) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.login_attempts = login_attempts;
        this.locked_until = locked_until;
        this.tokens = tokens;
        this.audit_logs = audit_logs;
        this.password_resets = password_resets;
        this.user_contracts = user_contracts;
        this.rol_id = rol_id;
    }

    public User(String name, String lastName, String email, String phoneNumber, String password, boolean status, LocalDateTime created_at, LocalDateTime updated_at, int login_attempts, LocalDateTime locked_until, List<Token> tokens, List<Audit_Logs> audit_logs, List<Password_Resets> password_resets, List<User_Contracts> user_contracts, Roles rol_id) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.login_attempts = login_attempts;
        this.locked_until = locked_until;
        this.tokens = tokens;
        this.audit_logs = audit_logs;
        this.password_resets = password_resets;
        this.user_contracts = user_contracts;
        this.rol_id = rol_id;
    }

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public int getLogin_attempts() {
        return login_attempts;
    }

    public void setLogin_attempts(int login_attempts) {
        this.login_attempts = login_attempts;
    }

    public LocalDateTime getLocked_until() { return locked_until; }
    public void setLocked_until(LocalDateTime locked_until) { this.locked_until = locked_until; }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Audit_Logs> getAudit_logs() {
        return audit_logs;
    }

    public void setAudit_logs(List<Audit_Logs> audit_logs) {
        this.audit_logs = audit_logs;
    }

    public List<Password_Resets> getPassword_resets() {
        return password_resets;
    }

    public void setPassword_resets(List<Password_Resets> password_resets) {
        this.password_resets = password_resets;
    }

    public List<User_Contracts> getUser_contracts() {
        return user_contracts;
    }

    public void setUser_contracts(List<User_Contracts> user_contracts) {
        this.user_contracts = user_contracts;
    }

    public Roles getRol_id() {
        return rol_id;
    }

    public void setRol_id(Roles rol_id) {
        this.rol_id = rol_id;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    private List<Audit_Logs> audit_logs;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    private List<Password_Resets> password_resets;

    @OneToMany(mappedBy = "user_id", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<User_Contracts> user_contracts;

    @ManyToOne
    @JsonIgnore
    private Roles rol_id;
}
