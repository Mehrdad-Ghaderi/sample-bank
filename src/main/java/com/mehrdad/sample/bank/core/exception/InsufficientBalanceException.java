package com.mehrdad.sample.bank.core.exception;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.Balance;

/**
 * Created by Mehrdad Ghaderi
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Insufficient balance.");
    }
}
