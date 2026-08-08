package com.fenil.projecthub.auth.dto;

import com.fenil.projecthub.user.dto.UserResponse;

public record LoginResponse(
        String accessToken,
        String  refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
