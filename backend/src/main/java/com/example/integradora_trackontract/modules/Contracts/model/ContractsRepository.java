package com.example.integradora_trackontract.modules.Contracts.model;

import com.example.integradora_trackontract.modules.Clients.model.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContractsRepository extends JpaRepository<Contracts, Long> {

    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id LEFT JOIN FETCH c.abogado_id")
    List<Contracts> findAllWithClientAndCategory();

    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id LEFT JOIN FETCH c.abogado_id WHERE c.status = :status")
    List<Contracts> findAllByStatusWithClientAndCategory(@Param("status") Boolean status);

    // Buscar contratos por abogado específico
    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id LEFT JOIN FETCH c.abogado_id WHERE c.abogado_id.id = :abogadoId")
    List<Contracts> findAllByAbogado(@Param("abogadoId") Long abogadoId);

    // Buscar contratos por cliente específico
    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id LEFT JOIN FETCH c.abogado_id WHERE c.client_id.id = :clientId")
    List<Contracts> findAllByClient(@Param("clientId") Long clientId);

    List<Contracts> findAllByStatusIsFalse();
    List<Contracts> findAllByStatusIsTrue();

    Optional<Contracts> findByName(String name);
    
    // Buscar contrato por nombre con todas las relaciones
    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id LEFT JOIN FETCH c.abogado_id WHERE c.name = :name")
    Optional<Contracts> findByNameWithRelations(@Param("name") String name);

    int countByStatusIsTrue();
    int countByStatusIsFalse();
    
    // Contar contratos activos por cliente
    @Query("SELECT COUNT(c) FROM Contracts c WHERE c.client_id.id = :clientId AND c.status = true")
    int countActiveContractsByClient(@Param("clientId") Long clientId);
    
    // Buscar contrato por ID con todas las relaciones
    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id LEFT JOIN FETCH c.abogado_id WHERE c.id = :id")
    Optional<Contracts> findByIdWithRelations(@Param("id") Long id);
}
