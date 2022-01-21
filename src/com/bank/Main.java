package com.bank;

import com.bank.repository.ClientRepository;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    public static Bank bank = Services.restoreBackup();
    public static ClientRepository clientRepository = bank.getClientRepository();

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
            if (userInput == 0) {
                System.out.println("The system was shut down bu the user.");
                backup();
                break;
            } else {
                System.out.println("The option you chose is not valid.");
            }
        }
    }

    private static void addNewClient() {
        System.out.println("CLIENT ADDITION:");
        System.out.println("Enter the ID: ");
        String id = getUserInputString();
        Client foundClient = clientRepository.findClientById(id);

        if (foundClient != null) {
            System.out.println("The client already exists in the bank > "+ foundClient.toString());
            return;
        }

        System.out.println("Enter the name:");
        String name = getUserInputString();
        System.out.println("Enter the phone number:");
        String phoneNumber = getUserInputString();

        System.out.println("Enter the currency:");
        String currency = getUserInputString();
        System.out.println("Enter The amount the client would like to deposit:");
        BigDecimal amount = getUserInputBigDecimal();

        Client newClient = new Client(name, phoneNumber, id);
        clientRepository.addClient(newClient);
    }

    private static void updatePhoneNumber() {
        System.out.println("CLIENT UPDATE:");
        System.out.println("Enter the ID of the client whose phone number you would like to update:");
        String id = getUserInputString();
        Client foundClient = clientRepository.findClientById(id);

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
        Client client = clientRepository.findClientById(id);

        if (client != null) {
            clientRepository.removeClient(client);
        }
    }

    private static void printAllClients() {
        clientRepository.viewAllClients();
    }

    private static void printMenu() {
        System.out.println("************************************************\n" +
                "Available Options:\n" +
                "Enter 1 to add a client.\n" +
                "Enter 2 to update a client's phone number\n" +
                "Enter 3 to remove a client.\n" +
                "Enter 4 to view all clients.\n");
              //  "Enter 5 to transfer money.\n" +
              //  "Enter 6 to deposit money.\n" +
              //  "Enter 7 to withdraw money.\n" +
              //  "Enter 8 to view the transactions of an account.\n" +
              // "Enter 9 to view the balance of an account.\n" +
              //  "Enter 10 to view the balance of the bank.\n" +

    }

    private static String getUserInputString() {
        return scanner.next();
    }

    private static BigDecimal getUserInputBigDecimal() {
        while (true) {
            if (scanner.hasNextBigDecimal()) {
                return scanner.nextBigDecimal();
            } else {
                System.out.println("What you entered is not valid.\n Try again:");
            }
            scanner.nextLine();
        }
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
        Services.backup(bank, Services.getPATH());
    }

}
