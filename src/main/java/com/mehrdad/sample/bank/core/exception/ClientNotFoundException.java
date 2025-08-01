package com.mehrdad.sample.bank.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Mehrdad Ghaderi
 */
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
