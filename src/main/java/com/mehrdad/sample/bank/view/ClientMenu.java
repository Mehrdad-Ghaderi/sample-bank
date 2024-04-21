package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.visitor.BalanceVisitor;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import com.mehrdad.sample.bank.core.service.TransactionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class ClientMenu extends Menu {
//figure out what to do with the last line on addNewClient()

    public ClientMenu(Scanner scanner, ClientService clientService, AccountService accountService, TransactionService transactionService, BalanceVisitor balanceVisitor) {
        super(scanner, clientService, accountService, transactionService, balanceVisitor);
    }


    @Override
    public void printMenu() {
        System.out.println("Client Menu:\n" +
                "************************************************\n" +
                "Available Options:\n" +
                "Enter 1 to add a client.\n" +
                "Enter 2 to update a client's phone number\n" +
                "Enter 3 to remove a client.\n" +
                "Enter 4 to view a client's details.\n" +
                "Enter 5 to view all clients.\n" +
                "Enter 6 to activate or deactivate a client's membership.\n"+
                "Enter 0 to Go Back"
        );
    }

    @Override
    public void run() {
        int userInput;
        while (true) {
            printMenu();
            userInput = getUserInputInt();
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
                printClient();
                continue;
            }
            if (userInput == 5) {
                printAllClients();
                continue;
            }
            if (userInput == 6) {
                activateOrDeactivateClient();
                continue;
            }
            if (userInput == 0) {
                homePage.setHomeMenu(homePage.getHomeMenu());
                homePage.run();
                break;
            }
        }
    }

    @Override
    public void runHomeMenu() {
        run();
    }

    protected void addNewClient() {
        System.out.println("New Client:");
        System.out.println("Enter the ID: ");
        String id = getUserInputString();

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
        String name = getUserInputString();
        System.out.println("Enter the phone number:");
        String phoneNumber = getUserInputString();
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
        String id = getUserInputString();

        Optional<ClientDto> client = clientService.getClientById(id);
        if (client.isEmpty()) {
            System.out.println("The client does not exist.");
            return;
        }

        ClientDto foundClient = client.get();
        System.out.println("Enter " + foundClient.getName() + "'s new phone number :");
        String newPhoneNumber = getUserInputString();
        System.out.println(foundClient.getName() + "'s old phone number, " + foundClient.getPhoneNumber() + ", was removed.");
        clientService.setClientPhoneNumber(id, newPhoneNumber);
        System.out.println("The new number has been set to " + newPhoneNumber + ".");
    }

    protected void removeClient() {
        System.out.println("CLIENT REMOVAL:");
        System.out.println("Enter the ID: ");
        String id = getUserInputString();

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
        String clientId = getUserInputString();
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
        String id = getUserInputString();
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
                userChoice = getUserInputString();
                if (userChoice.equals("A")) {
                    clientService.activateClient(client.getId());
                    System.out.println(client.getName() + " has been activated.");
                    return;
                }
            } else {
                System.out.println(client.getName() + " is active");
                System.out.println("Press D to DEACTIVATE the client's membership, or Q to go back to main menu.");
                userChoice = getUserInputString();
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
