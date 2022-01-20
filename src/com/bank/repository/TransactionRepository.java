package com.bank.repository;

import com.bank.Transaction;

import java.io.Serializable;
import java.util.ArrayList;

public class TransactionRepository implements Serializable {

    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

}
