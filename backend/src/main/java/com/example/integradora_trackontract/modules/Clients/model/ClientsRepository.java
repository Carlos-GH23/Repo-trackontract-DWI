package com.example.integradora_trackontract.modules.Clients.model;

import com.example.integradora_trackontract.modules.Categories.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientsRepository extends JpaRepository<Clients, Long> {
    List<Clients> findAllByStatusIsFalse();
    List<Clients> findAllByStatusIsTrue();

    Optional<Clients> findByName(String name);
    
    Optional<Clients> findByEmail(String email);

    int countByStatusIsTrue();

    int countByStatusIsFalse();
}
