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
    void testGetClientById() {
    }

    @Test
    void testGetAllClients() {
    }

    @Test
    void testSaveClient() {
    }

    @Test
    void testSetClientPhoneNumber() {
    }

    @Test
    void testRemoveClient() {
    }

    @Test
    void testActivateClient() {
    }

    @Test
    void testDeactivateClient() {
    }
}