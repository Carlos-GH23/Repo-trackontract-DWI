package com.example.integradora_trackontract.modules.Password_Resets.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface Password_ResetsRepository extends JpaRepository<Password_Resets, Long> {

    Optional<Password_Resets> findByToken(String token);

    @Query("""
           SELECT pr
           FROM Password_Resets pr
           WHERE pr.user_id.id = :userId AND pr.used_at IS NULL
           ORDER BY pr.created_at DESC
           """)
    List<Password_Resets> findActiveByUserId(Long userId);
}