package com.mehrdad.sample.bank.core.exception;

import com.mehrdad.sample.bank.core.entity.AccountEntity;

/**
 * Created by Mehrdad Ghaderi
 */
public class AccountNotActiveException extends RuntimeException {

    public AccountNotActiveException(AccountEntity account) {
        super("Account number: " + account.getNumber() + " is " + account.getStatus() + ".");
    }
}
