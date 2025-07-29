package com.example.integradora_trackontract.modules.Categories.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoriesDTO {
    @NotNull(groups = {Modify.class, ChangeStatus.class})
    private Long id;

    @NotBlank(groups = {Register.class, Modify.class})
    private String name;

    @NotBlank(groups = {Register.class, Modify.class})
    private String description;

    @NotNull(groups = {Register.class, ChangeStatus.class})
    private Boolean status;

    public CategoriesDTO() {
    }
    public CategoriesDTO(Long id, String name, String description, boolean status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public CategoriesDTO(String name, String description, boolean status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public interface Register{}
    public interface Modify{}
    public interface ChangeStatus{}
}
