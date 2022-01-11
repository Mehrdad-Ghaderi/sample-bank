package com.bank.repository;

import com.bank.Transaction;

import java.io.Serializable;
import java.util.ArrayList;

public class TransactionRepository implements Serializable {
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}
