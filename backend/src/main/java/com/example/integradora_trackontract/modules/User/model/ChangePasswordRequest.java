package com.example.integradora_trackontract.modules.User.model;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {
    @NotBlank private String currentPassword;
    @NotBlank private String newPassword;
    @NotBlank private String confirmNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }
    public void setCurrentPassword(String v) {
        this.currentPassword = v;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String v) {
        this.newPassword = v;
    }
    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }
    public void setConfirmNewPassword(String v) {
        this.confirmNewPassword = v;
    }
}
