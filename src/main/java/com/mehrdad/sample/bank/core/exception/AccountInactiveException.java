package com.mehrdad.sample.bank.core.exception;

public class AccountInactiveException extends RuntimeException {

    private final String accountNumber;

    public AccountInactiveException(String accountNumber) {
        super();
        this.accountNumber = accountNumber;
    }
}
