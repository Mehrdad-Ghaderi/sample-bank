package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyEntity {

    @Id
    @NotBlank
    @Size(max = 10)
    private String id;

    @NotNull
    private Currency currency;

    @NotNull
    @JoinColumn(name = "amount")
    private BigDecimal amount;

    public MoneyEntity(Currency currency, BigDecimal amount, AccountEntity account) {
        this.id = account.getNumber() + currency;
        this.currency = currency;
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "MoneyEntity{" +
                ", currency='" + currency + '\'' +
                ", balance=" + amount +
                '}';
    }

}
