package com.fenil.projecthub.auth.dto;

public record TokenPair(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}