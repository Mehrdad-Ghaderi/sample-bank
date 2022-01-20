package com.bank.repository;

import com.bank.Client;
import com.bank.Main;
import com.bank.Money;
import com.bank.Transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientRepository implements Serializable {

    private final ArrayList<Client> clients = new ArrayList<>(); // Abbas

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
            if (!foundClient.isMember()) {
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

        if (!client.isMember()) {
            System.out.println(client.getName() + " is no longer a member.\n The removal operation was unsuccessful.");
            return;
        }

        for (Money foundMoney : client.getAccount().getMoneys()) {

            if (foundMoney.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            System.out.println(foundMoney.getAmount() + " " + foundMoney.getCurrency() + " must be refunded to " + client.getName());
            Transaction transaction = new Transaction(foundMoney, client.getAccount(), Main.bank.getAccount());
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

}
