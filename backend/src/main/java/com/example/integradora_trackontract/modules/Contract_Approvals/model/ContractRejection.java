package com.example.integradora_trackontract.modules.Contract_Approvals.model;

import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.User.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_rejections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRejection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    @JsonIgnore
    private Contracts contract;
    
    @ManyToOne
    @JoinColumn(name = "rejected_by_abogado", nullable = false)
    private User rejectedByAbogado;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT", nullable = false)
    private String rejectionReason;
    
    @Column(name = "rejection_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime rejectionDate;
    
    @Column(name = "admin_reviewed", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean adminReviewed;
    
    @Column(name = "admin_comments", columnDefinition = "TEXT")
    private String adminComments;
    
    @Column(name = "admin_review_date")
    private LocalDateTime adminReviewDate;
    
    @ManyToOne
    @JoinColumn(name = "reviewed_by_admin")
    private User reviewedByAdmin;
    
    @Column(name = "final_decision", columnDefinition = "VARCHAR(50)")
    private String finalDecision; // "APPROVED", "REJECTED", "NEEDS_REVISION"
    
    public ContractRejection(Contracts contract, User rejectedByAbogado, String rejectionReason) {
        this.contract = contract;
        this.rejectedByAbogado = rejectedByAbogado;
        this.rejectionReason = rejectionReason;
        this.rejectionDate = LocalDateTime.now();
        this.adminReviewed = false;
    }
}
