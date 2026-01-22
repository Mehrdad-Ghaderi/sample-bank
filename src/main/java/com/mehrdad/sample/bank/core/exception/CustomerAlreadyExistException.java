package com.mehrdad.sample.bank.core.exception;

import java.util.UUID;

public class CustomerAlreadyExistException extends RuntimeException {
    public CustomerAlreadyExistException(UUID customerId) {
        super("Client with id " + customerId + " already exist.");
    }
}
