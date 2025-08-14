package com.example.integradora_trackontract.modules.Contracts.model;

import com.example.integradora_trackontract.modules.User.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad para registrar el historial de cambios de estado de los contratos
 */
@Entity
@Table(name = "contract_status_history")
public class ContractStatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    @JsonIgnore
    private Contracts contract;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private ContractStatus previousStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ContractStatus newStatus;
    
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    @JsonIgnore
    private User changedByUser;
    
    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
    
    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;

    // Constructores
    public ContractStatusHistory() {}
    
    public ContractStatusHistory(Contracts contract, ContractStatus previousStatus, 
                               ContractStatus newStatus, String changeReason, 
                               User changedByUser) {
        this.contract = contract;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changeReason = changeReason;
        this.changedByUser = changedByUser;
        this.changedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contracts getContract() {
        return contract;
    }

    public void setContract(Contracts contract) {
        this.contract = contract;
    }

    public ContractStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ContractStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ContractStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ContractStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public User getChangedByUser() {
        return changedByUser;
    }

    public void setChangedByUser(User changedByUser) {
        this.changedByUser = changedByUser;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "ContractStatusHistory{" +
                "id=" + id +
                ", contractId=" + (contract != null ? contract.getId() : null) +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", changeReason='" + changeReason + '\'' +
                ", changedByUserId=" + (changedByUser != null ? changedByUser.getId() : null) +
                ", changedAt=" + changedAt +
                '}';
    }
}
