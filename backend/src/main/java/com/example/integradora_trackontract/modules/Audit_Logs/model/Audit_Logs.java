package com.example.integradora_trackontract.modules.Audit_Logs.model;

import com.example.integradora_trackontract.modules.User.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs")
public class Audit_Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_name", columnDefinition = "VARCHAR(50)")
    private String entityName;

    @Column(name = "entity_id", columnDefinition = "BIGINT")
    private Long entityId;

    @Column(name = "action", columnDefinition = "VARCHAR(50)")
    private String action;

    @Column(name = "details", columnDefinition = "JSON")
    private String details;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String timestamp;

    @ManyToOne
    @JsonIgnore
    private User user_id;
}
