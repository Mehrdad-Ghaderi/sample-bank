package com.mehrdad.sample.bank.core.exception;

public class AccountNotFoundException extends RuntimeException{

    String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super("Account number " + accountNumber + " was not found.");
        this.accountNumber = accountNumber;
    }
}
