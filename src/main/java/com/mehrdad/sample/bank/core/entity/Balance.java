package com.mehrdad.sample.bank.core.entity;

import com.mehrdad.sample.bank.core.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    @Column(name="amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name="currency", nullable = false, length = 3)
    private Currency currency;


    public void decrease(BigDecimal value) {
        if (amount.compareTo(value) < 0) {
            throw new InsufficientBalanceException();
        }
        amount = amount.subtract(value);
    }

    public void increase(BigDecimal value) {
        amount = amount.add(value);
    }
}
