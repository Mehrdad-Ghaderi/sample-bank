package com.mehrdad.sample.bank.domain.exception.transaction;

public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException(){
        super("Currencies do not match.");
    }
}
