package com.mehrdad.sample.bank.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomerNameAlreadyExistsException extends RuntimeException {
    public CustomerNameAlreadyExistsException(String name) {
        super("Customer name '" + name + "' already exists.");
    }
}