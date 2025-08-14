package com.example.integradora_trackontract.modules.Contracts.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para el historial de cambios de estado de contratos
 */
@Repository
public interface ContractStatusHistoryRepository extends JpaRepository<ContractStatusHistory, Long> {
    
    /**
     * Buscar historial de cambios de estado por contrato
     */
    @Query("SELECT h FROM ContractStatusHistory h WHERE h.contract.id = :contractId ORDER BY h.changedAt DESC")
    List<ContractStatusHistory> findByContractIdOrderByChangedAtDesc(@Param("contractId") Long contractId);
    
    /**
     * Buscar historial de cambios de estado por usuario
     */
    @Query("SELECT h FROM ContractStatusHistory h WHERE h.changedByUser.id = :userId ORDER BY h.changedAt DESC")
    List<ContractStatusHistory> findByChangedByUserIdOrderByChangedAtDesc(@Param("userId") Long userId);
    
    /**
     * Buscar historial de cambios de estado por rango de fechas
     */
    @Query("SELECT h FROM ContractStatusHistory h WHERE h.changedAt BETWEEN :startDate AND :endDate ORDER BY h.changedAt DESC")
    List<ContractStatusHistory> findByChangedAtBetweenOrderByChangedAtDesc(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Buscar historial de cambios de estado por contrato y rango de fechas
     */
    @Query("SELECT h FROM ContractStatusHistory h WHERE h.contract.id = :contractId AND h.changedAt BETWEEN :startDate AND :endDate ORDER BY h.changedAt DESC")
    List<ContractStatusHistory> findByContractIdAndChangedAtBetweenOrderByChangedAtDesc(
            @Param("contractId") Long contractId,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Buscar el último cambio de estado de un contrato
     */
    @Query("SELECT h FROM ContractStatusHistory h WHERE h.contract.id = :contractId ORDER BY h.changedAt DESC")
    List<ContractStatusHistory> findLatestByContractId(@Param("contractId") Long contractId);
    
    /**
     * Contar cambios de estado por contrato
     */
    @Query("SELECT COUNT(h) FROM ContractStatusHistory h WHERE h.contract.id = :contractId")
    Long countByContractId(@Param("contractId") Long contractId);
    
    /**
     * Buscar cambios de estado por tipo de estado
     */
    @Query("SELECT h FROM ContractStatusHistory h WHERE h.newStatus = :status ORDER BY h.changedAt DESC")
    List<ContractStatusHistory> findByNewStatusOrderByChangedAtDesc(@Param("status") ContractStatus status);
    
    /**
     * Buscar cambios de estado por contrato y estado específico
     */
    @Query("SELECT h FROM ContractStatusHistory h WHERE h.contract.id = :contractId AND h.newStatus = :status ORDER BY h.changedAt DESC")
    List<ContractStatusHistory> findByContractIdAndNewStatusOrderByChangedAtDesc(
            @Param("contractId") Long contractId, 
            @Param("status") ContractStatus status);
}
