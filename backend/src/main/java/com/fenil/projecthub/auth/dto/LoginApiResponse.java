package com.fenil.projecthub.auth.dto;

import com.fenil.projecthub.user.dto.UserResponse;

public record LoginApiResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}