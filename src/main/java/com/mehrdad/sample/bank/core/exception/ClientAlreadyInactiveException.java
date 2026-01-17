package com.mehrdad.sample.bank.core.exception;

public class ClientAlreadyInactiveException extends RuntimeException {

    public ClientAlreadyInactiveException(String clientId) {
        super("Client with id: " + clientId + " is already inactive.");
    }
}
