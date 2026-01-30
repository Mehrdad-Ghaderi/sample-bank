package com.mehrdad.sample.bank.core.exception;

import org.springframework.dao.OptimisticLockingFailureException;

public class ConcurrentUpdateException extends RuntimeException {
    public ConcurrentUpdateException(String message, OptimisticLockingFailureException ex) {
        super(message);
    }
}
