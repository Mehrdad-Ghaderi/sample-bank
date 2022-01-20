package com.bank;

import java.io.Serializable;
import java.math.BigDecimal;

public class Money implements Serializable {

    private String currency;
    private BigDecimal amount = BigDecimal.ZERO;

    public Money(String currency, BigDecimal amount) {
        this.currency = currency;
        if (amount.compareTo(BigDecimal.ONE) > 0) {
            this.amount = this.amount.add(amount);
        } else {
            System.out.println("Negative numbers cannot be applied as an initial amount.\n" +
                    "This operation deposit was unsuccessful.");
        }
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Money{" +
                "currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}
