package com.bank.repository;

import com.bank.Client;

import java.util.ArrayList;

public class ClientRepository{

    private final ArrayList<Client> clients = new ArrayList<>();

    public Client getClientById(String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }

        System.out.println("No client with the ID of '" + id + "' was found.");
        return null;
    }

    public void addClient(Client newClient) {
        Client foundClient = getClientById(newClient.getId());

        if (foundClient != null) {
            System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", already exists in the bank");
            System.out.println("You can reactivate the member ship later in the menu.");
            return;
        }

        clients.add(newClient);
        System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", was added to the bank.");
    }

    public void removeClient(Client client) {
        if (client.isNotMember()) {
            System.out.println(client.getName() + " is no longer a member.\n The removal operation was unsuccessful.");
            return;
        }

        client.setMember(false);
        System.out.println(client.getName() + " is no longer a member and has been deleted from the bank repository");
    }

    public ArrayList<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

}
