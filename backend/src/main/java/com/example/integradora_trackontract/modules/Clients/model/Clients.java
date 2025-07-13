package com.example.integradora_trackontract.modules.Clients.model;

import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import jakarta.persistence.*;

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

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String updated_at;

    @OneToMany(mappedBy = "client_id", cascade = CascadeType.ALL)
    private List<Contracts> contracts;
}
