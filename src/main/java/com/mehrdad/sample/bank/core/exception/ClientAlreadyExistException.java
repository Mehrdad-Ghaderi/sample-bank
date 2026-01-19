package com.mehrdad.sample.bank.core.exception;

import jakarta.validation.constraints.Size;

public class ClientAlreadyExistException extends RuntimeException {
    public ClientAlreadyExistException(String clientId) {
        super("Client with id " + clientId + " already exist.");
    }
}
