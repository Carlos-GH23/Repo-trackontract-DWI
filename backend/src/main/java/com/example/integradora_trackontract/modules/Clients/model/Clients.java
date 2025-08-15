package com.example.integradora_trackontract.modules.Clients.model;

import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clients")
public class Clients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "business name", columnDefinition = "VARCHAR(50)")
    private String business_name;

    @Column(name = "representative_name", columnDefinition = "VARCHAR(100)")
    private String representative_name;

    @Column(name = "representative_surnames", columnDefinition = "VARCHAR(100)")
    private String representative_surnames;

    @Column(name = "email", columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(name = "phone", columnDefinition = "VARCHAR(15)")
    private String phone;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;


    @OneToMany(mappedBy = "client_id", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Contracts> contracts;

    public Clients() {
    }

    public Clients(Long id, String name, String business_name, String representative_name, String representative_surnames, String email, String phone, boolean status) {
        this.id = id;
        this.name = name;
        this.business_name = business_name;
        this.representative_name = representative_name;
        this.representative_surnames = representative_surnames;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public Clients(String name, String business_name, String representative_name, String representative_surnames, String email, String phone, boolean status) {
        this.name = name;
        this.business_name = business_name;
        this.representative_name = representative_name;
        this.representative_surnames = representative_surnames;
        this.email = email;
        this.phone = phone;
        this.status = status;
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

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getRepresentative_name() {
        return representative_name;
    }

    public void setRepresentative_name(String representative_name) {
        this.representative_name = representative_name;
    }

    public String getRepresentative_surnames() {
        return representative_surnames;
    }

    public void setRepresentative_surnames(String representative_surnames) {
        this.representative_surnames = representative_surnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public List<Contracts> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contracts> contracts) {
        this.contracts = contracts;
    }
}
