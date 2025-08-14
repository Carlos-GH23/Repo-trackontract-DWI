package com.example.integradora_trackontract.modules.Contracts.model;

import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Contract_Approvals.model.Contract_Approvals;
import com.example.integradora_trackontract.modules.User_Contracts.model.User_Contracts;
import com.example.integradora_trackontract.modules.User.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "contracts")
public class Contracts {
    
    public enum ApprovalStatus {
        PENDIENTE,
        ACEPTADO,
        RECHAZADO
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "due_date", columnDefinition = "DATE")
    private Date due_date;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDIENTE'")
    private ApprovalStatus approvalStatus;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

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

    @ManyToOne
    @JsonIgnore
    private User abogado_id;

    public Contracts() {
    }

    public Contracts(Long id, String name, String description, Date due_date, boolean status, LocalDateTime created_at, LocalDateTime updated_at, Categories categories, Clients clients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.due_date = due_date;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Contracts(String name, String description, Date due_date, boolean status, Clients clients, Categories categories) {
        this.name = name;
        this.description = description;
        this.due_date = due_date;
        this.status = status;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
        this.category_id = categories;
        this.client_id = clients;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
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

    public List<Contract_Approvals> getContract_approvals() {
        return contract_approvals;
    }

    public void setContract_approvals(List<Contract_Approvals> contract_approvals) {
        this.contract_approvals = contract_approvals;
    }

    public List<User_Contracts> getUser_contracts() {
        return user_contracts;
    }

    public void setUser_contracts(List<User_Contracts> user_contracts) {
        this.user_contracts = user_contracts;
    }

    public Categories getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Categories category_id) {
        this.category_id = category_id;
    }

    public Clients getClient_id() {
        return client_id;
    }

    public void setClient_id(Clients client_id) {
        this.client_id = client_id;
    }

    public User getAbogado_id() {
        return abogado_id;
    }

    public void setAbogado_id(User abogado_id) {
        this.abogado_id = abogado_id;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
