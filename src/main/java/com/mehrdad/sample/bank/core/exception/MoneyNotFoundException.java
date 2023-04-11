package com.mehrdad.sample.bank.core.exception;

import com.mehrdad.sample.bank.core.entity.MoneyEntity;

public class MoneyNotFoundException extends RuntimeException {

    private final MoneyEntity moneyEntity;

    public MoneyNotFoundException(MoneyEntity money) {
        super("There is no " + money.getCurrency() + " in account number " + money.getAccount().getNumber());
        this.moneyEntity = money;
    }

    public MoneyEntity getMoneyEntity() {
        return moneyEntity;
    }
}
