package com.bank;

import java.io.Serializable;

public class Transaction implements Serializable {

    private Money money;
    private Account sender;
    private Account receiver;

    public Transaction(Money money, Account sender, Account receiver) {
        this.money = money;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "money=" + money +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}
