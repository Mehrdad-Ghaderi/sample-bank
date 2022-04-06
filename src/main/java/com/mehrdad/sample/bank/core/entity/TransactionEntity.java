package com.mehrdad.sample.bank.core.entity;

import javax.persistence.*;

@Entity
public class TransactionEntity {

    private String id;
    private AccountEntity sender;
    private AccountEntity receiver;
    @OneToOne
    @JoinColumn(name = "money_id")
    private MoneyEntity moneyEntity;

    public MoneyEntity getMoneyEntity() {
        return moneyEntity;
    }

    public TransactionEntity(AccountEntity sender, AccountEntity receiver, MoneyEntity moneyEntity) {
        this.sender = sender;
        this.receiver = receiver;
        this.moneyEntity = moneyEntity;
    }

    public TransactionEntity() {
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "sender_id")
    public AccountEntity getSender() {
        return sender;
    }

    public void setSender(AccountEntity sender) {
        this.sender = sender;
    }

    @OneToOne
    @JoinColumn(name = "receiver_id")
    public AccountEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountEntity receiver) {
        this.receiver = receiver;
    }

    @OneToOne
    @JoinColumn(name = "amount")
    public MoneyEntity getMoney() {
        return moneyEntity;
    }

    public void setMoney(MoneyEntity moneyEntity) {
        this.moneyEntity = moneyEntity;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", sender=" + sender.getNumber() +
                ", receiver=" + receiver.getNumber() +
                ", amount=" + moneyEntity.getAmount() + " " + moneyEntity.getCurrency() +
                ", date= " +
                '}';
    }
}
