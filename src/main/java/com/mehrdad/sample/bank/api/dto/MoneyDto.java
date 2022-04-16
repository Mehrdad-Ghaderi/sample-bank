package com.mehrdad.sample.bank.api.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MoneyDto {

    private String id;
    private String currency;
    private BigDecimal amount;
    private AccountDto account;

    public MoneyDto(String currency, BigDecimal amount, AccountDto account) {
        this.id = account.getNumber() + currency;
        this.currency = currency;
        this.amount = amount;
        this.account = account;
    }

    public MoneyDto() {

    }

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
    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "MoneyDto{" +
                ", currency='" + currency + '\'' +
                ", balance=" + amount +
                ", account=" + account.getNumber() +
                '}';
    }
}

