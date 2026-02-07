package com.mehrdad.sample.bank.domain.exception.account;


import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account number " + accountNumber + " was not found.");
    }
    public AccountNotFoundException(UUID id) {
        super(id + " was not found.");
    }
}
