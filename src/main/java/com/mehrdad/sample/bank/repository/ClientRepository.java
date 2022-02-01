package com.mehrdad.sample.bank.repository;

import com.mehrdad.sample.bank.model.Client;

import java.util.*;

public class ClientRepository {

    private final Map<String, Client> clientMap = new HashMap<>();

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

        clientMap.put(newClient.getId(), newClient);
        System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", was added to the bank.");
    }

    public void removeClient(Client client) {
        if (client.isNotMember()) {
            System.out.println(client.getName() + " is not a member.\n The removal operation was unsuccessful.");
            return;
        }

        client.setMember(false);
        System.out.println(client.getName() + " is no longer a member and has been deactivated.");
    }

    public Client getClientById(String id) {
        return clientMap.get(id);
    }

    public Collection<Client> getAllClients() {
        return clientMap.values();
    }

    public void setClients(Collection<Client> clients) {
        clientMap.clear();
        for (Client client : clients) {
            clientMap.put(client.getId(), client);
        }
    }

}
