package com.mehrdad.sample.bank.domain.exception.user;

public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() {
        super("Current password is incorrect.");
    }
}
