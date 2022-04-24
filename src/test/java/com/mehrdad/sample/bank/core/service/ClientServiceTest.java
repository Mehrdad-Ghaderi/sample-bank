package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClientServiceTest {

    @BeforeAll
    static void createClient() {
        ClientEntity clientEntity = new ClientEntity("444", "Sam", "987456321", true);
    }

    @Test
    void getClientById() {

    }

    @Test
    void getAllClients() {

    }

    @Test
    void saveClient() {

    }

    @Test
    void setClientPhoneNumber() {

    }

    @Test
    void removeClient() {

    }

    @Test
    void activateClient() {

    }

    @Test
    void deactivateClient() {

    }
}