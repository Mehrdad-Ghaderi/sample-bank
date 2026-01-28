package com.mehrdad.sample.bank.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomerAlreadyInactiveException extends RuntimeException {

    public CustomerAlreadyInactiveException(UUID customerId) {
        super("Client with id: " + customerId + " is already inactive.");
    }
}
