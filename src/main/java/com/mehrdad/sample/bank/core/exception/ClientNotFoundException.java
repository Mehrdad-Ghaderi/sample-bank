package com.mehrdad.sample.bank.core.exception;


/**
 * Created by Mehrdad Ghaderi
 */
public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(String clientId) {
        super("Client with id " + clientId + " was not found");
    }
}
