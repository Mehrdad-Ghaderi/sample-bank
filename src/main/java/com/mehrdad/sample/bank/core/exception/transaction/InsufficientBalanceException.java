package com.mehrdad.sample.bank.core.exception.transaction;

/**
 * Created by Mehrdad Ghaderi
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Insufficient balance.");
    }
}
