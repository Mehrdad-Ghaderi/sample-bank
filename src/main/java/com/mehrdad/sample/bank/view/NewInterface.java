package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.service.ClientService;
import com.mehrdad.sample.bank.core.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 4/25/2025
 * Time: 12:59 AM
 */

@Component
@RequiredArgsConstructor
public class NewInterface {

    private final ClientService clientService;
    private final TransactionService transactionService;
    private final Utility utility;


    public void printMenu() {
        System.out.println("************************************************\n" +
                "Menu:\n" +
                "Enter 1 to add a client.\n" +
                "Enter 2 to update a client's phone number\n" +
                "Enter 3 to remove a client.\n" +
                "Enter 4 to view a client's details.\n" +
                "Enter 5 to view all clients.\n" +
                "Enter 6 to activate or deactivate a client's membership.\n" +
                "Enter 7 to create an account\n" +
                "Enter 8 to view information on an account:\n" +
                "Enter 9 to view all accounts in the bank:\n" +
                "Enter 10 to freeze or unfreeze an account:\n" +
                "Enter 11 to deposit money.\n" +
                "Enter 12 to withdraw money.\n" +
                "Enter 13 to transfer money.\n" +
                "Enter 14 to view the transactions of an account.\n" +
                "Enter 15 to view the balance of an account.\n" +
                "Enter 16 to view the balance of the bank.\n" +
                "Any other number to Go Back");
    }

    public void start(String[] args) {


        while (true) {
            int userInput;

            printMenu();
            userInput = utility.getUserInputInt();
            if (userInput == 1) {
                addNewClient();
            }
            if (userInput == 2) {
                updatePhoneNumber();
            }
            if (userInput == 3) {
                removeClient();
            }
            if (userInput == 4) {
                printClient();
            }
            if (userInput == 5) {
                printAllClients();
            }
            if (userInput == 6) {
                activateOrDeactivateClient();
            } else {
                break;
            }
        }

    }

    protected void addNewClient() {
        System.out.println("New Client:");
        System.out.println("Enter the ID: ");
        String id = utility.getUserInputString();

        Optional<ClientDto> client = clientService.getClientById(id);

        if (client.isPresent()) {
            System.out.println("The client is already a member of this bank.");

            ClientDto foundClient = client.get();
            if (!foundClient.isActive()) {
                activateOrDeactivateClient(foundClient);
            }
            return;
        }

        System.out.println("Enter the name:");
        String name = utility.getUserInputString();
        System.out.println("Enter the phone number:");
        String phoneNumber = utility.getUserInputString();
        ClientDto newClient = new ClientDto.Builder()
                .id(id)
                .name(name)
                .phoneNumber(phoneNumber)
                .active(true)
                .build();
        //ClientDto newClient = new ClientDto(id, name, phoneNumber, true);
        clientService.saveClient(newClient);
        System.out.println(newClient.getName() + " was added to the repository.");

        //made it static so it can also be accessed from ClientMenu but it didn't work
        //AccountMenu.createAccountFor(newClient);
    }

    protected void updatePhoneNumber() {
        System.out.println("CLIENT UPDATE:");
        System.out.println("Enter the ID of the client whose phone number you would like to update:");
        String id = utility.getUserInputString();

        Optional<ClientDto> client = clientService.getClientById(id);
        if (client.isEmpty()) {
            System.out.println("The client does not exist.");
            return;
        }

        ClientDto foundClient = client.get();
        System.out.println("Enter " + foundClient.getName() + "'s new phone number :");
        String newPhoneNumber = utility.getUserInputString();
        System.out.println(foundClient.getName() + "'s old phone number, " + foundClient.getPhoneNumber() + ", was removed.");
        clientService.setClientPhoneNumber(id, newPhoneNumber);
        System.out.println("The new number has been set to " + newPhoneNumber + ".");
    }

    protected void removeClient() {
        System.out.println("CLIENT REMOVAL:");
        System.out.println("Enter the ID: ");
        String id = utility.getUserInputString();

        Optional<ClientDto> client = clientService.getClientById(id);
        if (client.isEmpty()) {
            System.out.println("No client with that ID was found.");
            return;
        }

        ClientDto foundClient = client.get();
        if (!foundClient.isActive()) {
            System.out.println(foundClient.getName() + "'s membership status is already inactive.");
            return;
        }

        clientService.removeClient(foundClient);
        System.out.println(foundClient.getName() + "'s membership status has been deactivated.");
    }

    protected void printClient() {
        System.out.println("Enter the ID of the client you want information on:");
        String clientId = utility.getUserInputString();
        Optional<ClientDto> clientById = clientService.getClientById(clientId);
        clientById.ifPresentOrElse(System.out::println, () -> System.out.println("No client with that ID was found"));
    }

    protected void printAllClients() {
        List<ClientDto> allClients = clientService.getAllClients()
                .peek(System.out::println)
                .collect(Collectors.toList());

        System.out.println(allClients.size() + " clients were found!");
    }

    protected void activateOrDeactivateClient() {
        System.out.println("Enter the client ID:");
        String id = utility.getUserInputString();
        Optional<ClientDto> client = clientService.getClientById(id);
        if (client.isEmpty()) {
            System.out.println("No client with that ID was found.");
            return;
        }
        activateOrDeactivateClient(client.get());
    }

    protected void activateOrDeactivateClient(ClientDto client) {
        String userChoice = "";

        while (true) {
            if (!client.isActive()) {
                System.out.println(client.getName() + " is inactive");
                System.out.println("Press A to ACTIVATE the client's membership, or Q to go back to main menu.");
                userChoice = utility.getUserInputString();
                if (userChoice.equals("A")) {
                    clientService.activateClient(client.getId());
                    System.out.println(client.getName() + " has been activated.");
                    return;
                }
            } else {
                System.out.println(client.getName() + " is active");
                System.out.println("Press D to DEACTIVATE the client's membership, or Q to go back to main menu.");
                userChoice = utility.getUserInputString();
                if (userChoice.equals("D")) {
                    clientService.deactivateClient(client.getId());
                    System.out.println(client.getName() + " has been deactivated.");
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


}
