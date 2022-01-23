package com.bank.repository;

import com.bank.Client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientRepository implements Serializable {

    private final ArrayList<Client> clients = new ArrayList<>();

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

        Client foundClient = findClientById(newClient.getId());

        if (foundClient != null) {
            System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", already exists in the bank");
            if (foundClient.isNotMember()) {
                System.out.println("But " + foundClient.getName() + " is deactivated");

                System.out.println("Press A to reactivate the client's membership, any other key to leave it inactive:");
                String userChoice = new Scanner(System.in).next().toUpperCase();
                if (userChoice.equals("A")) {
                    foundClient.setMember(true);
                    System.out.println(foundClient.getName() + " has been reactivated.");
                }
            }
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

    public void printAllClients() {
        for (Client client : clients) {
            System.out.println(client.toString());
        }
        if (clients.isEmpty()) {
            System.out.println("The bank has no clients.");
        }
    }

}
