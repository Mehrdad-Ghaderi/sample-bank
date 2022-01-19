package com.bank.repository;

import com.bank.Client;
import com.bank.Main;
import com.bank.Money;
import com.bank.Transaction;

import java.io.Serializable;
import java.util.ArrayList;

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
        for (Client client : clients) {
            if (client.getId().equals(newClient.getId())) {
                System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", already exists in the bank");
                if (!client.isMember()) {
                    System.out.println("But " + client.getName() + " is deactivated");
                }
                return;
            }
        }
        clients.add(newClient);
        System.out.println(newClient.getName() + ", ID: " + newClient.getId() + ", was added to the bank.");
    }

    public void removeClient(Client client) {
        if (!client.isMember()) {
            System.out.println(client.getName() + " is no longer a member.\n This removal operation was unsuccessful.");
            return;
        }

        for (Money foundMoney : client.getAccount().getMoneys()) {
            System.out.println(foundMoney.getAmount() + " " + foundMoney.getCurrency() + " must be refunded to " + client.getName());
            Transaction transaction = new Transaction(foundMoney, Main.bank.getAccount(), client.getAccount());
            Main.bank.getTransactionRepository().getTransactions().add(transaction);
            foundMoney.setAmount(foundMoney.getAmount().subtract(foundMoney.getAmount()));
            System.out.println("The amount was refunded to the client, current balance: " + foundMoney.getAmount());
        }

        client.getAccount().setActive(false);
        System.out.println("The account was deactivated.");
        client.setMember(false);
        System.out.println(client.getName() + " is no longer a member and has been deleted from the bank repository");


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

}
