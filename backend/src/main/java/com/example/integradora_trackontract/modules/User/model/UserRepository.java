package com.example.integradora_trackontract.modules.User.model;

import com.example.integradora_trackontract.modules.Roles.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
