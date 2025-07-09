package com.example.integradora_trackontract.auth.controller;

public record LoginRequest(
        String email,
        String password
) {
}
