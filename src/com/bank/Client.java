package com.bank;

import java.io.Serializable;
import java.math.BigDecimal;

public class Client implements Serializable {
    private boolean isMember;
    private String name;
    private String id;
    private String phoneNumber;
    private Account account;

    Client(String name, String phoneNumber, String id, String currency, BigDecimal amount) {
        this.isMember = true;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.account = new Account(currency, amount);
        Main.bank.getAccountRepository().getAccounts().add(account);
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }



    @Override
    public String toString() {
        return "Client{" +
                "isMember=" + isMember +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", account=" + account +
                '}';
    }
}