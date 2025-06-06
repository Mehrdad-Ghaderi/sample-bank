package com.mehrdad.sample.bank.core.exception;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;

/**
 * Created by Mehrdad Ghaderi
 */
public class InsufficientBalanceException extends RuntimeException {

    private final MoneyEntity moneyEntity;

    public InsufficientBalanceException(AccountEntity accountEntity, MoneyEntity moneyEntity) {
        super("There is not "+ moneyEntity.getAmount()+" " + moneyEntity.getCurrency() + " available in account number: " + accountEntity + ".");
        this.moneyEntity = moneyEntity;
    }
}
