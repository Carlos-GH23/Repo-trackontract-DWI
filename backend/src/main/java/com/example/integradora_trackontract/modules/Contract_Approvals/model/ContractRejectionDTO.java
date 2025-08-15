package com.example.integradora_trackontract.modules.Contract_Approvals.model;

import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.User.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRejectionDTO {
    
    private Long id;
    private Long contractId;
    private String contractName;
    private String clientName;
    private String categoryName;
    private Long rejectedByAbogadoId;
    private String abogadoName;
    private String rejectionReason;
    private LocalDateTime rejectionDate;
    private boolean adminReviewed;
    private String adminComments;
    private LocalDateTime adminReviewDate;
    private Long reviewedByAdminId;
    private String adminReviewerName;
    private String finalDecision;
    
    // Constructor para crear DTO desde entidad
    public ContractRejectionDTO(ContractRejection rejection) {
        this.id = rejection.getId();
        this.contractId = rejection.getContract().getId();
        this.contractName = rejection.getContract().getName();
        this.clientName = rejection.getContract().getClient_id().getName();
        this.categoryName = rejection.getContract().getCategory_id().getName();
        this.rejectedByAbogadoId = rejection.getRejectedByAbogado().getId();
        this.abogadoName = rejection.getRejectedByAbogado().getName() + " " + rejection.getRejectedByAbogado().getLastName();
        this.rejectionReason = rejection.getRejectionReason();
        this.rejectionDate = rejection.getRejectionDate();
        this.adminReviewed = rejection.isAdminReviewed();
        this.adminComments = rejection.getAdminComments();
        this.adminReviewDate = rejection.getAdminReviewDate();
        this.reviewedByAdminId = rejection.getReviewedByAdmin() != null ? rejection.getReviewedByAdmin().getId() : null;
        this.adminReviewerName = rejection.getReviewedByAdmin() != null ? 
            rejection.getReviewedByAdmin().getName() + " " + rejection.getReviewedByAdmin().getLastName() : null;
        this.finalDecision = rejection.getFinalDecision();
    }
}
