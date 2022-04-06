package com.mehrdad.sample.bank.core.exception;

public class ClientNotFoundException extends RuntimeException {

    private final String clientId;

    public ClientNotFoundException(String clientId) {
        super("Client with id " + clientId + " was not found");
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

}
