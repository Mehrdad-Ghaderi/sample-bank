package com.bank.repository;

import com.bank.Account;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountRepository implements Serializable {

    private ArrayList<Account> accounts = new ArrayList<>();

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }
}
