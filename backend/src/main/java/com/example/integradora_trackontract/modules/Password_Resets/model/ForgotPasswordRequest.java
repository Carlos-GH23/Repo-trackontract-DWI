package com.example.integradora_trackontract.modules.Password_Resets.model;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email
    @NotBlank
    private String email;
}