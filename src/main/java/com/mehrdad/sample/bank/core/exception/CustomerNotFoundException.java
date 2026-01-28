package com.mehrdad.sample.bank.core.exception;


import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Client with id " + customerId + " was not found");
    }
}
