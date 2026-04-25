package com.mehrdad.sample.bank.security.exception;

public class InvalidLoginCredentialsException extends RuntimeException {
    public InvalidLoginCredentialsException() {
        super("Username or password is incorrect.");
    }
}
