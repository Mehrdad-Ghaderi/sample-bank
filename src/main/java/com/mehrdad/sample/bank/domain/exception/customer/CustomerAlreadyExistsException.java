package com.mehrdad.sample.bank.domain.exception.customer;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(UUID customerId) {
        super("Customer with id " + customerId + " already exists.");
    }

    public CustomerAlreadyExistsException(@NotBlank String phoneNumber) {
        super("Customer with phone number " + phoneNumber + " already exists.");
    }
}
