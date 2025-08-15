package com.example.integradora_trackontract.modules.Password_Resets.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordResponse {
    private String message;
    private String token;
}
