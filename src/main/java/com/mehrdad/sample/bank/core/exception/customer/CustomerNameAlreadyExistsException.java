package com.mehrdad.sample.bank.core.exception.customer;

public class CustomerNameAlreadyExistsException extends RuntimeException {
    public CustomerNameAlreadyExistsException(String name) {
        super("Customer name '" + name + "' already exists.");
    }
}