package com.example.integradora_trackontract.modules.Password_Resets.model;

import jakarta.validation.constraints.NotBlank;

public class ValidateTokenRequest {
    @NotBlank(message = "El token no puede estar vacío")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
