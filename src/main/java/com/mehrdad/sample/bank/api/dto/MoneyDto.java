package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.core.entity.Currency;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MoneyDto {

    private String id;
    private Currency currency;
    private BigDecimal amount;
    private AccountDto account;

    public MoneyDto(Currency currency, BigDecimal amount, AccountDto account) {
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
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
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

