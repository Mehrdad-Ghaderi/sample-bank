package com.bank.repository;

import com.bank.Account;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountRepository implements Serializable {

    private final ArrayList<Account> accounts = new ArrayList<>();

    public ArrayList<Account> getAccounts() {
        return accounts;
    }
    
}
