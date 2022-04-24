package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {

    @BeforeAll
    static void createClient() {
        ClientEntity clientEntity = new ClientEntity("444", "Sam", "987456321", true);
    }
    @Test
    void getClientById() {
        fail("This test has yet to be implemented.");
    }

    @Test
    void getAllClients() {
        fail("This test has yet to be implemented.");
    }

    @Test
    void saveClient() {
        fail("This test has yet to be implemented.");
    }

    @Test
    void setClientPhoneNumber() {
        fail("This test has yet to be implemented.");
    }

    @Test
    void removeClient() {
        fail("This test has yet to be implemented.");
    }

    @Test
    void activateClient() {
        fail("This test has yet to be implemented.");
    }

    @Test
    void deactivateClient() {
        fail("This test has yet to be implemented.");
    }
}