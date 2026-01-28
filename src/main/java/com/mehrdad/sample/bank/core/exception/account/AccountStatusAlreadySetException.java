package com.mehrdad.sample.bank.core.exception.account;

import com.mehrdad.sample.bank.core.entity.Status;

public class AccountStatusAlreadySetException extends RuntimeException {
    public AccountStatusAlreadySetException(Status status) {
        super("Account is already " + status + ".");
    }
}
