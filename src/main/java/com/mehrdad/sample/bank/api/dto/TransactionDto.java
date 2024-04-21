package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionDto {

    private Long id;
    private AccountDto sender;
    private AccountDto receiver;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime transactionTime;

    public TransactionDto(AccountDto sender, AccountDto receiver, BigDecimal amount, String currency) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
        this.transactionTime = LocalDateTime.now();
    }

    public TransactionDto(Builder builder) {
        this.id = builder.id;
        this.sender = builder.sender;
        this.receiver = builder.receiver;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.transactionTime = builder.transactionTime;
    }

    public TransactionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public AccountDto getSender() {
        return sender;
    }

    public void setSender(AccountDto sender) {
        this.sender = sender;
    }

    @NotNull
    public AccountDto getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountDto receiver) {
        this.receiver = receiver;
    }

    @NotNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @NotNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @NotNull
    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                ", Sender=" + sender.getClient().getName() + " " + sender.getNumber() +
                ", Receiver=" + receiver.getClient().getName() + " " + receiver.getNumber() +
                ", Amount=" + amount + currency +
                ", Time=" + transactionTime.format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss")) +
                '}';
    }

    public static class Builder{
        private Long id;
        private AccountDto sender;
        private AccountDto receiver;
        private BigDecimal amount;
        private String currency;
        private LocalDateTime transactionTime;

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sender(AccountDto sender) {
            this.sender = sender;
            return this;
        }
        public Builder receiver(AccountDto receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder transactionTie(LocalDateTime transactionTime) {
            this.transactionTime = transactionTime;
            return this;
        }

        public TransactionDto build() {
            return new TransactionDto(this);
        }
    }
}
