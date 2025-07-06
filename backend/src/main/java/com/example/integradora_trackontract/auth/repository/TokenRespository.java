package com.example.integradora_trackontract.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRespository extends JpaRepository<Token, Long> {


    List<Token> findAllValidIsFalseOrRevokedIsFalseByUserId(Long id);

    Optional<Token> findByToken(String token);
}
