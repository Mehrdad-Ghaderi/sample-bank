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

    public Account getSender() {
        return sender;
    }

    public Account getReceiver() {
        return receiver;
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
