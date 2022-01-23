package com.bank;

import com.bank.repository.ClientRepository;

import java.io.Serializable;

public class Bank implements Serializable {

    private String name = "TNB";
    private ClientRepository clientRepository = new ClientRepository();

    public Bank() {
    }

    public String getName() {
        return name;
    }

    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

}
