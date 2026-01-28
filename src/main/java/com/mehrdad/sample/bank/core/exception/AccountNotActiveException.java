package com.mehrdad.sample.bank.core.exception;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Mehrdad Ghaderi
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class AccountNotActiveException extends RuntimeException {

    public AccountNotActiveException(AccountEntity account) {
        super("Account number: " + account.getNumber() + " is " + account.getStatus() + ".");
    }
}
