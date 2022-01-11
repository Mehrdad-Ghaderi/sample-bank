package com.bank.repository;

import com.bank.Client;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientRepository implements Serializable {

    private ArrayList<Client> clients = new ArrayList<>();

    public Client findClientById(String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        System.out.println("No client with the ID of '" + id + "' was found.");
        return null;
    }

    public void addClient(Client newClient) {
        for (Client client : clients) {
            if (client.getId().equals(newClient.getId())) {
                System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", already exists in the bank");
                return;
            }
        }
        clients.add(newClient);
        System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", was added to the bank.");
    }

    public void removeClient(Client client) {
        if (findClientById(client.getId()) != null) {
            clients.remove(client);
            System.out.println(client.getName() + " has been deleted from the bank repository");
            return;
        }
        System.out.println(client.getName() + " does not exist in the bank repository.\n" +
                "Removal operation was unsuccessful.");
    }

    public void viewAllClients() {
        for (Client client : clients) {
            System.out.println(client.toString());
        }
        if (clients.isEmpty()) {
            System.out.println("The bank has no clients.");
        }
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
}
