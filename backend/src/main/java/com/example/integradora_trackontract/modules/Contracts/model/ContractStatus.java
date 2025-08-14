package com.example.integradora_trackontract.modules.Contracts.model;

/**
 * Enum que define todos los posibles estados de un contrato
 * y las transiciones permitidas entre estados
 */
public enum ContractStatus {
    
    // Estados iniciales
    DRAFT("Borrador", "Contrato en proceso de creación"),
    PENDING_REVIEW("Pendiente de Revisión", "Contrato enviado para revisión del abogado"),
    
    // Estados de revisión
    UNDER_REVIEW("En Revisión", "Abogado revisando el contrato"),
    NEEDS_REVISION("Necesita Revisión", "Contrato requiere modificaciones"),
    
    // Estados de aprobación
    PENDING_LAWYER_APPROVAL("Pendiente Aprobación Abogado", "Esperando aprobación del abogado"),
    PENDING_CLIENT_APPROVAL("Pendiente Aprobación Cliente", "Esperando aprobación del cliente"),
    
    // Estados finales
    APPROVED("Aprobado", "Contrato aprobado por ambas partes"),
    REJECTED("Rechazado", "Contrato rechazado"),
    CANCELLED("Cancelado", "Contrato cancelado"),
    
    // Estados de ejecución
    ACTIVE("Activo", "Contrato en ejecución"),
    COMPLETED("Completado", "Contrato finalizado exitosamente"),
    EXPIRED("Expirado", "Contrato vencido"),
    
    // Estados de suspensión
    SUSPENDED("Suspendido", "Contrato temporalmente suspendido"),
    TERMINATED("Terminado", "Contrato terminado anticipadamente");

    private final String displayName;
    private final String description;

    ContractStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica si la transición de estado es válida
     */
    public boolean canTransitionTo(ContractStatus newStatus) {
        switch (this) {
            case DRAFT:
                return newStatus == PENDING_REVIEW || newStatus == CANCELLED;
                
            case PENDING_REVIEW:
                return newStatus == UNDER_REVIEW || newStatus == CANCELLED;
                
            case UNDER_REVIEW:
                return newStatus == NEEDS_REVISION || newStatus == PENDING_LAWYER_APPROVAL || newStatus == CANCELLED;
                
            case NEEDS_REVISION:
                return newStatus == PENDING_REVIEW || newStatus == CANCELLED;
                
            case PENDING_LAWYER_APPROVAL:
                return newStatus == PENDING_CLIENT_APPROVAL || newStatus == REJECTED || newStatus == CANCELLED;
                
            case PENDING_CLIENT_APPROVAL:
                return newStatus == APPROVED || newStatus == REJECTED || newStatus == CANCELLED;
                
            case APPROVED:
                return newStatus == ACTIVE || newStatus == CANCELLED;
                
            case REJECTED:
                return newStatus == DRAFT || newStatus == CANCELLED;
                
            case ACTIVE:
                return newStatus == COMPLETED || newStatus == SUSPENDED || newStatus == EXPIRED || newStatus == TERMINATED;
                
            case SUSPENDED:
                return newStatus == ACTIVE || newStatus == TERMINATED;
                
            case COMPLETED:
            case EXPIRED:
            case TERMINATED:
            case CANCELLED:
                return false; // Estados finales, no se puede cambiar
                
            default:
                return false;
        }
    }

    /**
     * Obtiene los estados a los que se puede transicionar desde el estado actual
     */
    public ContractStatus[] getValidTransitions() {
        switch (this) {
            case DRAFT:
                return new ContractStatus[]{PENDING_REVIEW, CANCELLED};
                
            case PENDING_REVIEW:
                return new ContractStatus[]{UNDER_REVIEW, CANCELLED};
                
            case UNDER_REVIEW:
                return new ContractStatus[]{NEEDS_REVISION, PENDING_LAWYER_APPROVAL, CANCELLED};
                
            case NEEDS_REVISION:
                return new ContractStatus[]{PENDING_REVIEW, CANCELLED};
                
            case PENDING_LAWYER_APPROVAL:
                return new ContractStatus[]{PENDING_CLIENT_APPROVAL, REJECTED, CANCELLED};
                
            case PENDING_CLIENT_APPROVAL:
                return new ContractStatus[]{APPROVED, REJECTED, CANCELLED};
                
            case APPROVED:
                return new ContractStatus[]{ACTIVE, CANCELLED};
                
            case REJECTED:
                return new ContractStatus[]{DRAFT, CANCELLED};
                
            case ACTIVE:
                return new ContractStatus[]{COMPLETED, SUSPENDED, EXPIRED, TERMINATED};
                
            case SUSPENDED:
                return new ContractStatus[]{ACTIVE, TERMINATED};
                
            case COMPLETED:
            case EXPIRED:
            case TERMINATED:
            case CANCELLED:
                return new ContractStatus[]{};
                
            default:
                return new ContractStatus[]{};
        }
    }

    /**
     * Verifica si el estado es final (no se puede cambiar)
     */
    public boolean isFinal() {
        return this == COMPLETED || this == EXPIRED || this == TERMINATED || this == CANCELLED;
    }

    /**
     * Verifica si el estado permite edición del contrato
     */
    public boolean allowsEditing() {
        return this == DRAFT || this == NEEDS_REVISION || this == REJECTED;
    }

    /**
     * Verifica si el estado requiere aprobación
     */
    public boolean requiresApproval() {
        return this == PENDING_LAWYER_APPROVAL || this == PENDING_CLIENT_APPROVAL;
    }
}
