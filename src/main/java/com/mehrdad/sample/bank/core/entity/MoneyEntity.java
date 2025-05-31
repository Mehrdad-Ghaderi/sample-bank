package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.*;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
public class MoneyEntity {

    private String id;
    private Currency currency;
    private BigDecimal amount;
    //private AccountEntity account;

    public MoneyEntity(Currency currency, BigDecimal amount, AccountEntity account) {
        this.id = account.getNumber() + currency;
       // this.account = account;
        this.currency = currency;
        this.amount = amount;
    }

    public MoneyEntity() {
    }

    @Id
    @NotBlank
    @Size(max = 10)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @NotNull
    @JoinColumn(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal balance) {
        this.amount = balance;
    }

    /*@NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }*/

    @Override
    public String toString() {
        return "MoneyEntity{" +
                ", currency='" + currency + '\'' +
                ", balance=" + amount +
                /*", account=" + account.getNumber() +*/
                '}';
    }

}
