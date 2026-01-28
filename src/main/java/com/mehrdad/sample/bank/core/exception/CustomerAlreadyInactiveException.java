package com.mehrdad.sample.bank.core.exception;

import java.util.UUID;

public class CustomerAlreadyInactiveException extends RuntimeException {

    public CustomerAlreadyInactiveException(UUID customerId) {
        super("Client with id: " + customerId + " is already inactive.");
    }
}
