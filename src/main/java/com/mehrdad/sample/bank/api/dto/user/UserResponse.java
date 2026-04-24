package com.mehrdad.sample.bank.api.dto.user;

import com.mehrdad.sample.bank.domain.entity.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        UserRole role,
        boolean enabled,
        Instant createdAt
) {
}
