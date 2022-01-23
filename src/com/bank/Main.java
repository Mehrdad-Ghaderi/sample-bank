package com.bank;

import com.bank.repository.ClientRepository;
import com.bank.service.BackupService;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Bank bank = BackupService.restoreBackup();
    private static final ClientRepository clientRepository = bank.getClientRepository();

    public static void main(String[] args) {
        //This needs a go back option at all time, which is not written yet.
        while (true) {
            printMenu();

            int userInput = getUserInputInt();

            if (userInput == 1) {
                addNewClient();
                backup();
                continue;
            }
            if (userInput == 2) {
                updatePhoneNumber();
                backup();
                continue;
            }
            if (userInput == 3) {
                removeClient();
                backup();
                continue;
            }
            if (userInput == 4) {
                printAllClients();
                continue;
            }
            if (userInput == 5) {
                activateOrDeactivateClient();
                backup();
                continue;
            }
            if (userInput == 0) {
                System.out.println("The system was shut down by the user.");
                backup();
                break;
            } else {
                System.out.println("The option you chose is not valid.");
            }
        }
    }

    private static void activateOrDeactivateClient() {
        System.out.println("Enter the client ID:");
        String id = getUserInputString();
        Client foundClient = clientRepository.getClientById(id);
        if (foundClient == null) {
            return;
        }
        activateOrDeactivateClient(foundClient);
    }

    private static void addNewClient() {
        System.out.println("CLIENT ADDITION:");
        System.out.println("Enter the ID: ");
        String id = getUserInputString();
        Client foundClient = clientRepository.getClientById(id);

        if (foundClient != null) {
            System.out.println("The client already exists in the bank > " + foundClient.toString());

            if (foundClient.isNotMember()) {
                activateOrDeactivateClient(foundClient);
                return;
            }

            return;
        }

        System.out.println("Enter the name:");
        String name = getUserInputString();
        System.out.println("Enter the phone number:");
        String phoneNumber = getUserInputString();

        Client newClient = new Client(name, phoneNumber, id);
        clientRepository.addClient(newClient);
    }

    private static void activateOrDeactivateClient(Client client) {
        String userChoice;

        while (true) {
            if (client.isNotMember()) {
                System.out.println(client.getName() + " is inactive");
                System.out.println("Press A to ACTIVATE the client's membership:");
                userChoice = scanner.next().toUpperCase();
                if (userChoice.equals("A")) {
                    client.setMember(true);
                    System.out.println(client.getName() + " has been activated.");
                }
                return;

            }

            System.out.println(client.getName() + " is active");
            System.out.println("Press D to DEACTIVATE the client's membership,");
            userChoice = scanner.next().toUpperCase();
            if (userChoice.equals("D")) {
                client.setMember(false);
                System.out.println(client.getName() + " has been deactivated.");
                return;
            }

            System.out.println("The input value was NOT valid.\n Please try again.");
        }
    }

    private static void updatePhoneNumber() {
        System.out.println("CLIENT UPDATE:");
        System.out.println("Enter the ID of the client whose phone number you would like to update:");
        String id = getUserInputString();
        Client foundClient = clientRepository.getClientById(id);

        if (foundClient != null) {
            System.out.println("Enter the new phone number:");
            String newPhoneNumber = getUserInputString();
            System.out.println(foundClient.getName() + "'s old phone number," + foundClient.getPhoneNumber() + ", was removed.");
            foundClient.setPhoneNumber(newPhoneNumber);
            System.out.println(foundClient.getName() + "'s new number has been set to " + newPhoneNumber + ".");
        }
    }

    private static void removeClient() {
        System.out.println("CLIENT REMOVAL:");
        System.out.println("Enter the ID: ");
        String id = getUserInputString();
        Client client = clientRepository.getClientById(id);

        if (client != null) {
            clientRepository.removeClient(client);
        }
    }

    private static void printAllClients() {
        ArrayList<Client> clients = clientRepository.getAllClients();
        for (Client client : clients) {
            System.out.println(client.toString());
        }
        if (clients.isEmpty()) {
            System.out.println("The bank has no clients.");
        }
    }

    private static void printMenu() {
        System.out.println("************************************************\n" +
                "Available Options:\n" +
                "Enter 1 to add a client.\n" +
                "Enter 2 to update a client's phone number\n" +
                "Enter 3 to remove a client.\n" +
                "Enter 4 to view all clients.\n" +
                "Enter 5 to activate a client's membership.\n");
//                "Enter 6 to transfer money.\n" +
//                "Enter 7 to deposit money.\n" +
//                "Enter 8 to withdraw money.\n" +
//                "Enter 9 to view the transactions of an account.\n" +
//                "Enter 10 to view the balance of an account.\n" +
//                "Enter 11 to view the balance of the bank.\n" +

    }

    private static String getUserInputString() {
        return scanner.next();
    }

    private static int getUserInputInt() {
        while (true) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {

                System.out.println("Please enter ONLY numbers.\nTry again:");
            }
            scanner.nextLine();
        }
    }

    private static void backup() {
        BackupService.backup(bank, BackupService.getPATH());
    }

}
