package com.mehrdad.sample.bank.core.exception;

import com.mehrdad.sample.bank.core.entity.MoneyEntity;

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
