package com.bank;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Account implements Serializable {

    private int accountNumber;
    private boolean active;
    private ArrayList<Money> moneys = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public Account(String currency, BigDecimal amount) {
        this.active = true;
        Bank.accountCounter++;
        this.accountNumber = Bank.getAccountCounter() + 100;
        System.out.println("Account number '" + this.accountNumber + "' was specified to the client.");
        Money money = new Money(currency, amount);
        moneys.add(money);
        System.out.println(money.getAmount() + " " + money.getCurrency() + " was deposited into the bank account.");
        Transaction transaction = new Transaction(money, this, Main.bank.getAccount());
        Main.bank.getTransactionRepository().getTransactions().add(transaction);
        System.out.println(transaction.toString());
    }

    public Account() { // only for the bank account

    }

    public Money findMoneyByCurrency(String currency) {
        for (Money foundMoney : moneys) {
            if (foundMoney.getCurrency().equals(currency)) {
                return foundMoney;
            }
        }
        return null;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public ArrayList<Money> getMoneys() {
        return moneys;
    }

    public void setMoneys(ArrayList<Money> moneys) {
        this.moneys = moneys;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", active=" + active +
                ", moneys=" + moneys +
                ", transactions=" + transactions +
                '}';
    }
}
