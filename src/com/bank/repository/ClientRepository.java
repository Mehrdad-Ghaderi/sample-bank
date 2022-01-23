package com.bank.repository;

import com.bank.model.Client;

import java.util.HashMap;
import java.util.Map;

public class ClientRepository {

    private final Map<String, Client> clients = new HashMap<>();

    public Client getClientById(String id) {
        return clients.get(id);
    }

    public void addClient(Client newClient) {
        Client foundClient = getClientById(newClient.getId());

        if (foundClient != null) {
            System.out.println(foundClient.getName() + ", ID: " + foundClient.getId() + ", already exists in the bank");
            if (foundClient.isNotMember()) {
                System.out.println("But their membership status has been set to inactive.");
                System.out.println("You can reactivate the membership later in the main menu.");
            }
            return;
        }

        clients.put(newClient.getId(), newClient);
        System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", was added to the bank.");
    }

    public void removeClient(Client client) {
        if (client.isNotMember()) {
            System.out.println(client.getName() + " is not a member.\n The removal operation was unsuccessful.");
            return;
        }

        client.setMember(false);
        System.out.println(client.getName() + " is no longer a member and has been deleted from the bank repository");
    }

    public Map<String, Client> getAllClients() {
        return clients;
    }

}
