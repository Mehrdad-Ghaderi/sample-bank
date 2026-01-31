package com.mehrdad.sample.bank.core.exception.transaction;


import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Mehrdad Ghaderi
 */
@Getter
public class InvalidAmountException extends RuntimeException {

    private final BigDecimal amount;

    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount: " + amount);
        this.amount = amount;
    }
}
