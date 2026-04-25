package com.mehrdad.sample.bank.domain.exception.user;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super("User with id " + userId + " was not found.");
    }
}
