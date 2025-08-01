package com.mehrdad.sample.bank.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Mehrdad Ghaderi
 */
public class AccountInactiveException extends RuntimeException {

    private final String accountNumber;

    public AccountInactiveException(String accountNumber) {
        super("Account number: " + accountNumber + " is not active.");
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
