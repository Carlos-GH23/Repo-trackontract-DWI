package com.example.integradora_trackontract.modules.Contract_Approvals.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContractRejectionRepository extends JpaRepository<ContractRejection, Long> {
    
    // Buscar rechazos por contrato
    Optional<ContractRejection> findByContractId(Long contractId);
    
    // Buscar todos los rechazos pendientes de revisión del admin
    List<ContractRejection> findByAdminReviewedFalseOrderByRejectionDateDesc();
    
    // Buscar rechazos por abogado
    List<ContractRejection> findByRejectedByAbogadoIdOrderByRejectionDateDesc(Long abogadoId);
    
    // Buscar rechazos por contrato con relaciones
    @Query("SELECT cr FROM ContractRejection cr " +
           "JOIN FETCH cr.contract c " +
           "JOIN FETCH cr.rejectedByAbogado a " +
           "WHERE cr.contract.id = :contractId")
    Optional<ContractRejection> findByContractIdWithRelations(@Param("contractId") Long contractId);
    
    // Contar rechazos pendientes de revisión
    long countByAdminReviewedFalse();
}
