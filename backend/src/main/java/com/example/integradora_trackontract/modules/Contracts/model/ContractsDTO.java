package com.example.integradora_trackontract.modules.Contracts.model;

import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Categories.model.CategoriesDTO;
import com.example.integradora_trackontract.modules.Clients.model.ClientsDTO;
import com.example.integradora_trackontract.modules.User.model.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class ContractsDTO {

    @NotNull(groups = {Modify.class, ChangeStatus.class})
    private Long id;

    @NotBlank(groups = {Register.class, Modify.class})
    private String name;

    @NotBlank(groups = {Register.class, Modify.class})
    private String description;

    @NotNull(groups = {Register.class, Modify.class})
    private Date due_date;

    @NotNull(groups = {Register.class, ChangeStatus.class})
    private Boolean status;

    private String approvalStatus;

    private String approvedAt;

    private String rejectionReason;
    
    // Nuevo campo para el estado del contrato
    private String contractStatus;

    @NotNull(groups = {Register.class, Modify.class}, message = "El cliente no puede ser nulo")
    private ClientsDTO clientsDTO;

    @NotNull(groups = {Register.class, Modify.class}, message = "La categoria no puede ser nula")
    private CategoriesDTO categoriesDTO;

    @NotNull(groups = {Register.class, Modify.class}, message = "El abogado no puede ser nulo")
    private UserDTO abogadoDTO;

    public ContractsDTO() {
    }

    public ContractsDTO(Long id, String name, String description, Date due_date, Boolean status, ClientsDTO clientsDTO, CategoriesDTO categoriesDTO, UserDTO abogadoDTO) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.due_date = due_date;
        this.status = status;
        this.clientsDTO = clientsDTO;
        this.categoriesDTO = categoriesDTO;
        this.abogadoDTO = abogadoDTO;
    }

    public ContractsDTO(String name, String description, Date due_date, Boolean status, ClientsDTO clientsDTO, CategoriesDTO categoriesDTO, UserDTO abogadoDTO) {
        this.name = name;
        this.description = description;
        this.due_date = due_date;
        this.status = status;
        this.clientsDTO = clientsDTO;
        this.categoriesDTO = categoriesDTO;
        this.abogadoDTO = abogadoDTO;
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

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ClientsDTO getClientsDTO() {
        return clientsDTO;
    }

    public void setClientsDTO(ClientsDTO clientsDTO) {
        this.clientsDTO = clientsDTO;
    }

    public CategoriesDTO getCategoriesDTO() {
        return categoriesDTO;
    }

    public void setCategoriesDTO(CategoriesDTO categoriesDTO) {
        this.categoriesDTO = categoriesDTO;
    }

    public UserDTO getAbogadoDTO() {
        return abogadoDTO;
    }

    public void setAbogadoDTO(UserDTO abogadoDTO) {
        this.abogadoDTO = abogadoDTO;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public interface Register {}
    public interface  Modify {}
    public interface ChangeStatus {}
}
