package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.core.entity.Currency;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Mehrdad Ghaderi
 */
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

    public MoneyDto(Builder builder) {
        this.id = builder.id;
        this.currency = builder.currency;
        this.amount = builder.amount;
        this.account = builder.account;
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

    public static class Builder {
        private String id;
        private Currency currency;
        private BigDecimal amount;
        private AccountDto account;

        public Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder account(AccountDto account) {
            this.account = account;
            return this;
        }

        public MoneyDto build() {
            return new MoneyDto(this);
        }
    }

}

