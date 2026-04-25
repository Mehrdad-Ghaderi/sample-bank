package com.mehrdad.sample.bank.api.dto.user;

import com.mehrdad.sample.bank.domain.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank
        @Size(max = 100)
        String username,

        @NotBlank
        @Size(min = 8, max = 72)
        String password,

        @NotNull
        UserRole role
) {
}
