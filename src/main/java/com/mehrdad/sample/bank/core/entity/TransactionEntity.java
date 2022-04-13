package com.mehrdad.sample.bank.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class TransactionEntity {

    private String id;
    private AccountEntity sender;
    private AccountEntity receiver;
    private MoneyEntity money;

    public TransactionEntity(AccountEntity sender, AccountEntity receiver, MoneyEntity money) {
        this.sender = sender;
        this.receiver = receiver;
        this.money = money;
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

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", sender=" + sender.getNumber() +
                ", receiver=" + receiver.getNumber() +
                ", amount=" + money.getAmount() + " " + money.getCurrency() +
                ", date= " +
                '}';
    }
}
