package com.mehrdad.sample.bank.domain.exception.transaction;

/**
 * Created by Mehrdad Ghaderi
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Insufficient balance.");
    }
}
