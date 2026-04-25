package com.mehrdad.sample.bank.security;

import java.time.Instant;

public class TooManyLoginAttemptsException extends RuntimeException {

    private final Instant retryAt;

    public TooManyLoginAttemptsException(Instant retryAt) {
        super("Too many failed login attempts. Try again later.");
        this.retryAt = retryAt;
    }

    public Instant getRetryAt() {
        return retryAt;
    }
}
