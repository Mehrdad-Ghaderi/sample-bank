package com.mehrdad.sample.bank.domain.exception.transaction;

public class InvalidIdempotencyKeyException extends RuntimeException {
    public InvalidIdempotencyKeyException() {
        super("Idempotency-Key header must not be blank");
    }
}
