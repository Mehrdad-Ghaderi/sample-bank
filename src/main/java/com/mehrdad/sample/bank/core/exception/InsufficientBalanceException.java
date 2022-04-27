package com.mehrdad.sample.bank.core.exception;

import com.mehrdad.sample.bank.core.entity.MoneyEntity;

public class InsufficientBalanceException extends RuntimeException {

    private final MoneyEntity moneyEntity;

    public InsufficientBalanceException(MoneyEntity moneyEntity) {
        super("There is not enough " + moneyEntity.getCurrency() + " in account number: " + moneyEntity.getAccount() + ".");
        this.moneyEntity = moneyEntity;
    }
}
