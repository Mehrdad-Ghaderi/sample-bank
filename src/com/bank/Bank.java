package com.bank;

import com.bank.repository.AccountRepository;
import com.bank.repository.ClientRepository;
import com.bank.repository.TransactionRepository;
import java.io.Serializable;

public class Bank implements Serializable {

    static int accountCounter = 0;
    private String name = "TNB";
    private Account account = new Account();
    private ClientRepository clientRepository = new ClientRepository();
    private AccountRepository accountRepository = new AccountRepository();
    private TransactionRepository transactionRepository = new TransactionRepository();

    public Bank() {
        this.account.setAccountNumber(100);
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }

    public static int getAccountCounter() {
        return accountCounter;
    }

    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public static void setAccountCounter(int accountCounter) {
        Bank.accountCounter = accountCounter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
