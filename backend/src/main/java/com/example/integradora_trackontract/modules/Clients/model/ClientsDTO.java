package com.example.integradora_trackontract.modules.Clients.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClientsDTO {
    @NotNull(groups = {Modify.class, ChangeStatus.class})
    private Long id;

    @NotBlank(groups = {Register.class, Modify.class})
    private String name;

    @NotBlank(groups = {Register.class, Modify.class})
    private String business_name;

    @NotBlank(groups = {Register.class, Modify.class})
    private String representative_name;

    @NotBlank(groups = {Register.class, Modify.class})
    private String representative_surnames;

    @Email(groups = {Register.class, Modify.class})
    private String email;

    @NotBlank(groups = {Register.class, Modify.class})
    private String phone;

    @NotNull(groups = {Register.class, ChangeStatus.class})
    private Boolean status;

    public ClientsDTO() {
    }

    public ClientsDTO(Long id, String name, String business_name, String representative_name, String representative_surnames, String email, String phone, Boolean status) {
        this.id = id;
        this.name = name;
        this.business_name = business_name;
        this.representative_name = representative_name;
        this.representative_surnames = representative_surnames;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public ClientsDTO(String name, String business_name, String representative_name, String representative_surnames, String email, String phone, Boolean status) {
        this.name = name;
        this.business_name = business_name;
        this.representative_name = representative_name;
        this.representative_surnames = representative_surnames;
        this.email = email;
        this.phone = phone;
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

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getRepresentative_name() {
        return representative_name;
    }

    public void setRepresentative_name(String representative_name) {
        this.representative_name = representative_name;
    }

    public String getRepresentative_surnames() {
        return representative_surnames;
    }

    public void setRepresentative_surnames(String representative_surnames) {
        this.representative_surnames = representative_surnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public interface Register {}
    public interface  Modify {}
    public interface ChangeStatus {}
}
