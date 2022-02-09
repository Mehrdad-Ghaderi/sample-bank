package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.service.ClientService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserInterface {

    private final Scanner scanner;
    private final ClientService clientService;

    public UserInterface(Scanner scanner, ClientService clientService) {
        this.scanner = scanner;
        this.clientService = clientService;
    }

    public void start(String[] args) {

        //This needs a go back option at all time, which is not written yet.
        while (true) {
            printMenu();
            int userInput = getUserInputInt();

            if (userInput == 1) {
                addNewClient();
                continue;
            }
            if (userInput == 2) {
                updatePhoneNumber();
                continue;
            }
            if (userInput == 3) {
                removeClient();
                continue;
            }
            if (userInput == 4) {
                printAllClients();
                continue;
            }
            if (userInput == 5) {
                activateOrDeactivateClient();
                continue;
            }
            if (userInput == 0) {
                System.out.println("The system was shut down by the user.");
                break;
            } else {
                System.out.println("The option you chose is not valid.");
            }
        }
    }

    private void addNewClient() {
        System.out.println("CLIENT ADDITION:");
        System.out.println("Enter the ID: ");
        String id = getUserInputString();

        ClientDto client = clientService.getClientById(id);

        if (client != null) {
            System.out.println("The client is already a member of this bank.");

            if (!client.isActive()) {
                activateOrDeactivateClient(client);
            }
            return;
        }

        System.out.println("Enter the name:");
        String name = getUserInputString();
        System.out.println("Enter the phone number:");
        String phoneNumber = getUserInputString();

        ClientDto newClient = new ClientDto(id, name, phoneNumber, true);
        clientService.createClientEntity(newClient);
        System.out.println(newClient.getName() + " was added to the repository.");
    }

    private void updatePhoneNumber() {
        System.out.println("CLIENT UPDATE:");
        System.out.println("Enter the ID of the client whose phone number you would like to update:");
        String id = getUserInputString();
        ClientDto foundClient = clientService.getClientById(id);

        if (foundClient != null) {
            System.out.println("Enter " + foundClient.getName() + "'s new phone number :");
            String newPhoneNumber = getUserInputString();
            System.out.println(foundClient.getName() + "'s old phone number, " + foundClient.getPhoneNumber() + ", was removed.");
            clientService.setPhoneNumber(id, newPhoneNumber);
            System.out.println("The new number has been set to " + newPhoneNumber + ".");
        }
    }

    private void removeClient() {
        System.out.println("CLIENT REMOVAL:");
        System.out.println("Enter the ID: ");
        String id = getUserInputString();
        ClientDto foundClientDto = clientService.getClientById(id);

        if (foundClientDto == null) {
            System.out.println("No client with that ID was found.");
            return;
        }

        if (!foundClientDto.isActive()) {
            System.out.println(foundClientDto.getName() + "'s membership status is already inactive.");
            return;
        }

        clientService.setActive(id, false);
        System.out.println(foundClientDto.getName() + "'s membership status has been set to inactive.");
    }

    private void printAllClients() {
        Collection<ClientDto> clientDtos = clientService.getAllClientDtos();

        if (clientDtos.isEmpty()) {
            System.out.println("The bank has no clients.");
            return;
        }

        for (ClientDto clientDto : clientDtos) {
                System.out.println(clientDto);
        }
    }

    private void activateOrDeactivateClient() {
        System.out.println("Enter the client ID:");
        String id = getUserInputString();
        ClientDto foundClientDto = clientService.getClientById(id);
        if (foundClientDto == null) {
            System.out.println("No client with that ID was found.");
            return;
        }
        activateOrDeactivateClient(foundClientDto);
    }

    private void activateOrDeactivateClient(ClientDto clientDto) {
        String userChoice = "";

        while (true) {
            if (!clientDto.isActive()) {
                System.out.println(clientDto.getName() + " is inactive");
                System.out.println("Press A to ACTIVATE the client's membership:");
                System.out.println("Press Q to go back to main menu.");
                userChoice = scanner.next().toUpperCase();
                if (userChoice.equals("A")) {
                    clientService.setActive(clientDto.getId(),true);
                    System.out.println(clientDto.getName() + " has been activated.");
                    return;
                }
            }

            if (clientDto.isActive()) {
                System.out.println(clientDto.getName() + " is active");
                System.out.println("Press D to DEACTIVATE the client's membership,");
                System.out.println("Press Q to go back to main menu.");
                userChoice = scanner.next().toUpperCase();
                if (userChoice.equals("D")) {
                    clientService.setActive(clientDto.getId(), false);
                    System.out.println(clientDto.getName() + " has been deactivated.");
                    return;
                }
            }

            if (userChoice.equals("Q")) {
                System.out.println("Operation was canceled.");
                break;
            }
            System.out.println("The input value was NOT valid.\nPlease try again.");
        }
    }

    private void printMenu() {
        System.out.println("************************************************\n" +
                "Available Options:\n" +
                "Enter 1 to add a client.\n" +
                "Enter 2 to update a client's phone number\n" +
                "Enter 3 to remove a client.\n" +
                "Enter 4 to view all clients.\n" +
                "Enter 5 to activate or deactivate a client's membership.\n" +
//                "Enter 6 to transfer money.\n" +
//                "Enter 7 to deposit money.\n" +
//                "Enter 8 to withdraw money.\n" +
//                "Enter 9 to view the transactions of an account.\n" +
//                "Enter 10 to view the balance of an account.\n" +
//                "Enter 11 to view the balance of the bank.\n" +
                "Enter 0 to shut down");
    }

    private String getUserInputString() {
        while (true) {
            try {
                return scanner.next().toUpperCase();
            } catch (NoSuchElementException e) {
                System.err.println("Unsupported characters.\n Try again:");
                scanner.nextLine();
            }
        }
    }

    private int getUserInputInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter ONLY numbers.\nTry again:");
                scanner.nextLine();
            }
        }
    }

}
