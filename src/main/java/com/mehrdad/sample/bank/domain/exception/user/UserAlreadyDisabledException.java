package com.mehrdad.sample.bank.domain.exception.user;

import java.util.UUID;

public class UserAlreadyDisabledException extends RuntimeException {
    public UserAlreadyDisabledException(UUID userId) {
        super("User with id " + userId + " is already disabled.");
    }
}
