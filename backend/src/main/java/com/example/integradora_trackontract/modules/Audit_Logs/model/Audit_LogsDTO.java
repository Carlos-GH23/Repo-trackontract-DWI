package com.example.integradora_trackontract.modules.Audit_Logs.model;

public record Audit_LogsDTO(
        Long id,
        String usuario,
        String rol,
        String method,
        String path,
        String fecha
) {}
