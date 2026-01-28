package com.mehrdad.sample.bank.core.exception;


import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account number " + accountNumber + " was not found.");
    }
    public AccountNotFoundException(UUID customerId) {
        super("No account is associated with customer: " + customerId + ".");
    }
}
