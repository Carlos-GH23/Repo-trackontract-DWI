package com.example.integradora_trackontract.modules.Categories.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    List<Categories> findAllByStatusIsFalse();

    Optional<Categories> findByName(String name);

    List<Categories>findAllByStatusIsTrue();



}
