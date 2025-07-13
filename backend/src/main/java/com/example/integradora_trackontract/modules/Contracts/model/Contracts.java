package com.example.integradora_trackontract.modules.Contracts.model;

import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Contract_Approvals.model.Contract_Approvals;
import com.example.integradora_trackontract.modules.User_Contracts.model.User_Contracts;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "contracts")
public class Contracts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "due_date", columnDefinition = "DATE")
    private String due_date;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String updated_at;

    @OneToMany(mappedBy = "contract_id", cascade = CascadeType.ALL)
    private List<Contract_Approvals> contract_approvals;

    @OneToMany(mappedBy = "contract_id", cascade = CascadeType.ALL)
    private List<User_Contracts> user_contracts;

    @ManyToOne
    @JsonIgnore
    private Categories category_id;

    @ManyToOne
    @JsonIgnore
    private Clients client_id;
}
