package com.mehrdad.sample.bank.exception.security;

import lombok.Getter;

import java.time.Instant;

@Getter
public class TooManyLoginAttemptsException extends RuntimeException {

    private final Instant retryAt;

    public TooManyLoginAttemptsException(Instant retryAt) {
        super("Too many failed login attempts. Try again later.");
        this.retryAt = retryAt;
    }
}
