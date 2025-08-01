package com.mehrdad.sample.bank.core.exception;


/**
 * Created by Mehrdad Ghaderi
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account number " + accountNumber + " was not found.");
    }
}
