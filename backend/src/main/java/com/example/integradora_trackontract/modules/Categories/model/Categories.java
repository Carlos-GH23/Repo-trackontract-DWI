package com.example.integradora_trackontract.modules.Categories.model;

import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    @OneToMany(mappedBy = "category_id", cascade = CascadeType.ALL)
    private List<Contracts> contracts;
}
