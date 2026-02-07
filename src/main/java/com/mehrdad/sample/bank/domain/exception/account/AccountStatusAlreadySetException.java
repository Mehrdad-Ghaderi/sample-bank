package com.mehrdad.sample.bank.domain.exception.account;

import com.mehrdad.sample.bank.domain.entity.Status;

public class AccountStatusAlreadySetException extends RuntimeException {
    public AccountStatusAlreadySetException(Status status) {
        super("Account is already " + status + ".");
    }
}
