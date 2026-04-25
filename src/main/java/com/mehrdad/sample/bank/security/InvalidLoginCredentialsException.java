package com.mehrdad.sample.bank.security;

public class InvalidLoginCredentialsException extends RuntimeException {
    public InvalidLoginCredentialsException() {
        super("Username or password is incorrect.");
    }
}
