package com.mehrdad.sample.bank.domain.exception.customer;

import java.util.UUID;

public class CustomerAlreadyInactiveException extends RuntimeException {

    public CustomerAlreadyInactiveException(UUID customerId) {
        super("Client with id: " + customerId + " is already inactive.");
    }
}
