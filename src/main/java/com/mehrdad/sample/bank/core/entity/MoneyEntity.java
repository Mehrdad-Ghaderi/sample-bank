package com.mehrdad.sample.bank.core.entity;

import javax.persistence.*;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @NotBlank
    @Size(max = 10)
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
    @JoinColumn(name = "amount")
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
