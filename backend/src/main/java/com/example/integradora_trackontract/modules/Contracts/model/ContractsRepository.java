package com.example.integradora_trackontract.modules.Contracts.model;

import com.example.integradora_trackontract.modules.Clients.model.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContractsRepository extends JpaRepository<Contracts, Long> {

    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id")
    List<Contracts> findAllWithClientAndCategory();

    @Query("SELECT c FROM Contracts c LEFT JOIN FETCH c.client_id LEFT JOIN FETCH c.category_id WHERE c.status = :status")
    List<Contracts> findAllByStatusWithClientAndCategory(@Param("status") Boolean status);

    // Proyección simple que solo incluye los campos necesarios
    @Query("SELECT c.id as id, c.name as name, c.description as description, c.due_date as due_date, c.status as status, " +
           "c.client_id.id as clientId, c.client_id.name as clientName, " +
           "c.category_id.id as categoryId, c.category_id.name as categoryName " +
           "FROM Contracts c")
    List<Object[]> findAllContractsWithBasicInfo();

    @Query("SELECT c.id as id, c.name as name, c.description as description, c.due_date as due_date, c.status as status, " +
           "c.client_id.id as clientId, c.client_id.name as clientName, " +
           "c.category_id.id as categoryId, c.category_id.name as categoryName " +
           "FROM Contracts c WHERE c.status = :status")
    List<Object[]> findAllContractsByStatusWithBasicInfo(@Param("status") Boolean status);

    List<Contracts> findAllByStatusIsFalse();
    List<Contracts> findAllByStatusIsTrue();

    Optional<Contracts> findByName(String name);

    int countByStatusIsTrue();

    int countByStatusIsFalse();
}
