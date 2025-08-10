package com.example.integradora_trackontract.modules.User.model;

import com.example.integradora_trackontract.modules.Roles.model.RolesDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDTO {

    @NotNull(groups = {Modify.class, ChangeStatus.class})
    private Long id;

    @NotBlank(groups = {Register.class, Modify.class})
    private String name;

    @NotBlank(groups = {Register.class, Modify.class})
    private String last_name;

    @NotBlank(groups = {Register.class, Modify.class})
    private String email;

    @NotNull(groups = {Register.class, ChangeStatus.class})
    private String phoneNumber;

    @NotNull(groups = {Register.class, ChangeStatus.class})
    private String password;

    @NotNull(groups = {Register.class, ChangeStatus.class})
    private Boolean status;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String last_name, String email, String phoneNumber, String password, Boolean status) {
        this.id = id;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.status = status;
    }

    public UserDTO(String name, String last_name, String email, String phoneNumber, String password, Boolean status) {
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
