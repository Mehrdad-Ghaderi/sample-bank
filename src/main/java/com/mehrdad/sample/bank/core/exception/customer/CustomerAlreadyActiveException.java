package com.mehrdad.sample.bank.core.exception.customer;

import java.util.UUID;

public class CustomerAlreadyActiveException extends RuntimeException{
    public CustomerAlreadyActiveException(UUID customerId) {
        super("Client with id " + customerId + " is already active");
    }
}
