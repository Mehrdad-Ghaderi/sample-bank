package com.mehrdad.sample.bank.api.dto.auth;

import java.time.Instant;

public record TokenResponse(
        String tokenType,
        String accessToken,
        Instant expiresAt
) {
}
