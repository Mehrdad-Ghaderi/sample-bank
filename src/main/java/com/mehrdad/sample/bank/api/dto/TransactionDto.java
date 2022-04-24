package com.mehrdad.sample.bank.api.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionDto {

    private String id;
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
        this.id = transactionTime.toString();
    }

    public TransactionDto() {
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return "TransactionDto{" +
                "id='" + id + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", money=" + amount + currency +
                ", local date time=" + transactionTime.format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss")) +
                '}';
    }

}
