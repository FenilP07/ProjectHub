package com.fenil.projecthub.user.dto;

import com.fenil.projecthub.user.domain.UserRole;
import com.fenil.projecthub.user.domain.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}