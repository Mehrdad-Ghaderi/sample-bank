package com.mehrdad.sample.bank.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class TransactionEntity {

    private String id;
    private AccountEntity sender;
    private AccountEntity receiver;
    private MoneyEntity money;
    private LocalDateTime localDateTime;

    public TransactionEntity(AccountEntity sender, AccountEntity receiver, MoneyEntity money) {
        this.sender = sender;
        this.receiver = receiver;
        this.money = money;
        this.localDateTime = LocalDateTime.now();
    }

    public TransactionEntity() {
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
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    public AccountEntity getSender() {
        return sender;
    }

    public void setSender(AccountEntity sender) {
        this.sender = sender;
    }

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    public AccountEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountEntity receiver) {
        this.receiver = receiver;
    }

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "money_id")
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
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", money=" + money.getAmount() + money.getCurrency() +
                ", local date time=" + localDateTime.format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss")) +
                '}';
    }
}
