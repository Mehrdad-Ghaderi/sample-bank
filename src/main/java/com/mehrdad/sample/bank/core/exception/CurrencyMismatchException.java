package com.mehrdad.sample.bank.core.exception;

public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException(){
        super("Currencies do not match.");
    }
}
