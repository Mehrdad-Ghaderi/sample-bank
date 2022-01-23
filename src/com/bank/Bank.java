package com.bank;

import com.bank.repository.ClientRepository;

import java.io.Serializable;

public class Bank implements Serializable {

    static int accountCounter = 0;
    private String name = "TNB";
    private ClientRepository clientRepository = new ClientRepository();

    public Bank() {
    }

    public String getName() {
        return name;
    }

    public static int getAccountCounter() {
        return accountCounter;
    }

    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    public static void setAccountCounter(int accountCounter) {
        Bank.accountCounter = accountCounter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

}
