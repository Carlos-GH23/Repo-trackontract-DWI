package com.example.integradora_trackontract.modules.Contracts.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para manejar cambios de estado de contratos
 */
public class ContractStatusChangeDTO {
    
    @NotNull(message = "El ID del contrato es obligatorio")
    private Long contractId;
    
    @NotNull(message = "El nuevo estado es obligatorio")
    private ContractStatus newStatus;
    
    @NotBlank(message = "La razón del cambio es obligatoria")
    private String changeReason;
    
    private String additionalNotes;
    
    @NotNull(message = "El ID del usuario que realiza el cambio es obligatorio")
    private Long changedByUserId;

    // Constructores
    public ContractStatusChangeDTO() {}
    
    public ContractStatusChangeDTO(Long contractId, ContractStatus newStatus, 
                                 String changeReason, Long changedByUserId) {
        this.contractId = contractId;
        this.newStatus = newStatus;
        this.changeReason = changeReason;
        this.changedByUserId = changedByUserId;
    }
    
    public ContractStatusChangeDTO(Long contractId, ContractStatus newStatus, 
                                 String changeReason, String additionalNotes, 
                                 Long changedByUserId) {
        this.contractId = contractId;
        this.newStatus = newStatus;
        this.changeReason = changeReason;
        this.additionalNotes = additionalNotes;
        this.changedByUserId = changedByUserId;
    }

    // Getters y Setters
    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    @Override
    public String toString() {
        return "ContractStatusChangeDTO{" +
                "contractId=" + contractId +
                ", newStatus=" + newStatus +
                ", changeReason='" + changeReason + '\'' +
                ", additionalNotes='" + additionalNotes + '\'' +
                ", changedByUserId=" + changedByUserId +
                '}';
    }
}
