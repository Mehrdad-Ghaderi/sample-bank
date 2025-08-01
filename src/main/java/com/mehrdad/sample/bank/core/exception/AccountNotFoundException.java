package com.mehrdad.sample.bank.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Mehrdad Ghaderi
 */
public class AccountNotFoundException extends RuntimeException {

    String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super("Account number " + accountNumber + " was not found.");
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
