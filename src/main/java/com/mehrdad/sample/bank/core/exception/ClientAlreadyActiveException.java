package com.mehrdad.sample.bank.core.exception;

public class ClientAlreadyActiveException extends RuntimeException{
    public ClientAlreadyActiveException(String clientId) {
        super("Client with id " + clientId + " is already active");
    }
}
