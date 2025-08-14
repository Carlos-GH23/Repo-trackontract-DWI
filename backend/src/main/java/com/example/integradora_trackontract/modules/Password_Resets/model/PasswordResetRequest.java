package com.example.integradora_trackontract.modules.Password_Resets.model;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank
    private String token;

    @NotBlank
    private String newPassword;
}