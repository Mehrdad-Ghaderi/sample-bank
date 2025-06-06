package com.mehrdad.sample.bank.core.exception;

import java.math.BigDecimal;

/**
 * Created by Mehrdad Ghaderi
 */
public class InvalidAmountException extends RuntimeException {

    private final BigDecimal amount;

    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount: " + amount);
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
