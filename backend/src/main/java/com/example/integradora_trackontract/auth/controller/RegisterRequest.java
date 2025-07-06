package com.example.integradora_trackontract.auth.controller;

public record RegisterRequest(
        String email,
        String password,
        String name
) {

}
