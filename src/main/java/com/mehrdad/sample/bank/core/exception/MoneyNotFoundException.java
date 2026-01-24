package com.mehrdad.sample.bank.core.exception;


import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.Currency;

/**
 * Created by Mehrdad Ghaderi
 */
public class MoneyNotFoundException extends RuntimeException {

    public MoneyNotFoundException(String message) {
        super(message);
    }

    public MoneyNotFoundException(AccountEntity account, Currency currency) {
        super("There is no " + currency + " in account number " + account.getNumber());
    }
}
