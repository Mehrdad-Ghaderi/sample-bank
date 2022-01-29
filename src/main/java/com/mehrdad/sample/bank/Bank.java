package com.mehrdad.sample.bank;

import com.mehrdad.sample.bank.repository.ClientRepository;

import java.io.Serializable;

public class Bank implements Serializable {

    private ClientRepository clientRepository = new ClientRepository();

    public Bank() {
    }

    ClientRepository getClientRepository() {
        return clientRepository;
    }

}
