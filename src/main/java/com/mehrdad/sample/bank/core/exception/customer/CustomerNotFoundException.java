package com.mehrdad.sample.bank.core.exception.customer;


import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Client with id: " + customerId + " was not found");
    }

    public CustomerNotFoundException(Integer businessId) {
        super("Client with business id: " + businessId + " was not found");

    }

    public CustomerNotFoundException() {

    }
}
