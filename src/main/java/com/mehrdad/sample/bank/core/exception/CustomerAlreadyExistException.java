package com.mehrdad.sample.bank.core.exception;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class CustomerAlreadyExistException extends RuntimeException {
    public CustomerAlreadyExistException(UUID customerId) {
        super("Client with id " + customerId + " already exists.");
    }

    public CustomerAlreadyExistException(@NotBlank String phoneNumber) {
        super("Client with that phone number already exists.");
    }
}
