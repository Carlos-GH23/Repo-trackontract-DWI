package com.example.integradora_trackontract.auth.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("token") String token,
        @JsonProperty("user") UserInfo user
) {
    public record UserInfo(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("role") String role
    ) {}
}
