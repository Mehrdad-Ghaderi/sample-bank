package com.mehrdad.sample.bank.domain.exception.account;

import com.mehrdad.sample.bank.domain.entity.AccountEntity;

/**
 * Created by Mehrdad Ghaderi
 */
public class AccountNotActiveException extends RuntimeException {

    public AccountNotActiveException(AccountEntity account) {
        super("Account number: " + account.getNumber() + " is " + account.getStatus() + ".");
    }
}
