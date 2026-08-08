package com.fenil.projecthub.auth.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}