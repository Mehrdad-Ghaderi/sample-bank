package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.core.entity.MoneyEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionDto {

    private String id;
    private AccountDto sender;
    private AccountDto receiver;
    private MoneyEntity money;
    private LocalDateTime localDateTime;

    public TransactionDto(AccountDto sender, AccountDto receiver, MoneyEntity money) {
        this.sender = sender;
        this.receiver = receiver;
        this.money = money;
        this.localDateTime = LocalDateTime.now();
    }

    public TransactionDto() {
    }

    @Id
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
    public MoneyEntity getMoney() {
        return money;
    }

    public void setMoney(MoneyEntity money) {
        this.money = money;
    }

    @NotNull
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "id='" + id + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", money=" + money.getAmount() + money.getCurrency() +
                ", local date time=" + localDateTime.format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss")) +
                '}';
    }

}
