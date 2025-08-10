package com.example.integradora_trackontract.modules.User.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Busqueda de usuarios inactivos
    List<User> findAllByStatusIsFalse();

    Optional<User> findByName(String name);

    //Busqueda de usuarios activos
    List<User> findAllByStatusIsTrue();

}
