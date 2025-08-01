package com.mehrdad.sample.bank.core.exception;


/**
 * Created by Mehrdad Ghaderi
 */
public class MoneyNotFoundException extends RuntimeException {

    public MoneyNotFoundException(String currency, String accountNumber) {
        super("There is no " + currency + " in account number " + accountNumber);
    }

    public MoneyNotFoundException(String message) {
        super(message);
    }}
