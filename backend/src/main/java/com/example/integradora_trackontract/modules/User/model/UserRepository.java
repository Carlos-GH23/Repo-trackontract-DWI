package com.example.integradora_trackontract.modules.User.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Busqueda de usuarios inactivos
    List<User> findAllByStatusIsFalse();

    Optional<User> findByName(String name);

    //Busqueda de usuarios activos
    List<User> findAllByStatusIsTrue();

    // Buscar usuarios por rol específico
    @Query("SELECT u FROM User u JOIN u.rol_id r WHERE r.name = :roleName AND u.status = true")
    List<User> findAllByRoleNameAndStatusActive(@Param("roleName") String roleName);
}
