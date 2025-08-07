package com.example.integradora_trackontract.modules.Contracts.model;

import com.example.integradora_trackontract.modules.Clients.model.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractsRepository extends JpaRepository<Contracts, Long> {

    List<Contracts> findAllByStatusIsFalse();
    List<Contracts> findAllByStatusIsTrue();

    Optional<Contracts> findByName(String name);

    int countByStatusIsTrue();

    int countByStatusIsFalse();
}
