package com.example.integradora_trackontract.auth.controller;

import java.sql.Timestamp;

public record RegisterRequest(
        String email,
        String password,
        String name,

        String lastName,

        String phoneNumber,

        Boolean status,

        Timestamp createdAt,

        Timestamp updatedAt


) {

}
