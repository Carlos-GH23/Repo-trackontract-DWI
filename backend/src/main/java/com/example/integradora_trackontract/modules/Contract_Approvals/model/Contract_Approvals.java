package com.example.integradora_trackontract.modules.Contract_Approvals.model;

import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "contract_approvals")
public class Contract_Approvals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approved_by_abogado", columnDefinition = "BOOLEAN")
    private boolean approved_by_abogado;

    @Column(name = "approved_by_client", columnDefinition = "BOOLEAN")
    private boolean approved_by_client;

    @Column(name = "rejected_by_abogado", columnDefinition = "BOOLEAN")
    private boolean rejected_by_abogado;

    @Column(name = "rejected_reason", columnDefinition = "VARCHAR(255)")
    private String rejected_reason;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String updated_at;

    @ManyToOne
    @JsonIgnore
    private Contracts contract_id;
}
