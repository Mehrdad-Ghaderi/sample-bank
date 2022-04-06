package com.mehrdad.sample.bank.core.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class MoneyEntity {

    private String id;
    private String currency;
    private BigDecimal amount;
    private AccountEntity account;

    public MoneyEntity(String currency, BigDecimal amount, AccountEntity account) {
        this.id = account.getNumber() + currency;
        this.currency = currency;
        this.amount = amount;
        this.account = account;
    }

    public MoneyEntity() {
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @NotNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal balance) {
        this.amount = balance;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "MoneyEntity{" +
                ", currency='" + currency + '\'' +
                ", balance=" + amount +
                ", account=" + account.getNumber() +
                '}';
    }
}
