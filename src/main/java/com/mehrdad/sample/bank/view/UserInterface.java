package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
public class UserInterface {

    private final Scanner scanner;
    private final ClientService clientService;
    private final AccountService accountService;

    public UserInterface(Scanner scanner, ClientService clientService, AccountService accountService) {
        this.scanner = scanner;
        this.clientService = clientService;
        this.accountService = accountService;
    }

    private void printMenu() {
        System.out.println("************************************************\n" +
                "Available Options:\n" +
                "Enter 1 to add a client.\n" +
                "Enter 2 to update a client's phone number\n" +
                "Enter 3 to remove a client.\n" +
                "Enter 4 to view detailed information on a client.\n" +
                "Enter 5 to view all clients.\n" +
                "Enter 6 to activate or deactivate a client's membership.\n" +
                "Enter 7 to create an account\n" +
                "Enter 8 to view information on an account:\n" +
                "Enter 9 to view all accounts in the bank:\n" +
                "Enter 10 to freeze or unfreeze an account:\n" +
//                "Enter  to deposit money.\n" +
//                "Enter  to withdraw money.\n" +
//                "Enter  to transfer money.\n" +
//                "Enter  to view the transactions of an account.\n" +
//                "Enter  to view the balance of an account.\n" +
//                "Enter  to view the balance of the bank.\n" +
                "Enter 0 to shut down");
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
            if (userInput == 7) {
                createAccount();
            }
            if (userInput == 8) {
                printAccount();
                continue;
            }
            if (userInput == 9) {
                printAllAccounts();
                continue;
            }
            if (userInput == 10) {
                freezeOrUnfreezeAccount();
                continue;
            }
            if (userInput == 0) {
                System.out.println("The system was shut down by the user.");
                break;
            }
        }
    }

    private void addNewClient() {
        System.out.println("CLIENT ADDITION:");
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

        ClientDto newClient = new ClientDto(id, name, phoneNumber, true);
        clientService.saveClient(newClient);
        System.out.println(newClient.getName() + " was added to the repository.");
    }

    private void updatePhoneNumber() {
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

    private void removeClient() {
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

    private void printClient() {
        System.out.println("Enter the ID of the client you want information on:");
        String clientId = getUserInputString();
        Optional<ClientDto> clientById = clientService.getClientById(clientId);
        clientById.ifPresentOrElse(System.out::println, () -> System.out.println("No client with that ID was found"));
    }

    private void printAllClients() {
        List<ClientDto> allClients = clientService.getAllClients()
                .peek(System.out::println)
                .collect(Collectors.toList());

        System.out.println(allClients.size() + " clients were found!");
    }

    private void activateOrDeactivateClient() {
        System.out.println("Enter the client ID:");
        String id = getUserInputString();
        Optional<ClientDto> client = clientService.getClientById(id);
        if (client.isEmpty()) {
            System.out.println("No client with that ID was found.");
            return;
        }

        activateOrDeactivateClient(client.get());
    }

    private void activateOrDeactivateClient(ClientDto client) {
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

    private void createAccount() {
        System.out.println("Account Setup:\n" +
                "Enter the ID of the client you want to open an account for:");
        String clientID = getUserInputString();
        Optional<ClientDto> foundClient = clientService.getClientById(clientID);
        if (foundClient.isEmpty()) {
            System.out.println("No client with the ID, " + clientID + ", was found.");
            return;
        }
        ClientDto client = foundClient.get();
        List<AccountDto> accounts = ofNullable(client.getAccounts()).orElseGet(Collections::emptyList);

        if (accounts.isEmpty()) {
            System.out.printf("No account has been previously allocated to %s%n", client.getName());
        } else {
            System.out.printf("Here is a list of all available accounts of %s:%s%n", client.getName(), accounts);
        }

        while (true) {
            System.out.println("Enter an account number to allocate to the client:");
            String accountNumber = getUserInputString();
            if (accounts.stream().anyMatch(account -> account.getNumber().equals(accountNumber))) {
                System.out.println("Account number " + accountNumber + " has already been allocated to " + client.getName() + ".");
                continue;
            }


            if (accountService.createAccount(accountNumber, client)) {
                System.out.println("Account number " + accountNumber + " was allocated to " + client.getName() + " .");
            }
            break;
        }
    }

    private void printAccount() {
        System.out.println("Enter the account number you want information on:");
        String accountNumber = getUserInputString();
        Optional<AccountDto> account = accountService.getAccountByAccountNumber(accountNumber);
        account.ifPresentOrElse(System.out::println, () -> System.out.println("No account with that number was found."));
    }

    private void printAllAccounts() {
        accountService.getAllAccounts().forEach(System.out::println);
    }

    private void freezeOrUnfreezeAccount() {
        System.out.println("Enter the account number you would like to freeze:");
        String accountNumber = getUserInputString();
        Optional<AccountDto> account = accountService.getAccountByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            System.out.println("Account number, " + accountNumber + ", was NOT fount.");
            return;
        }
        freezeOrUnfreezeAccount(account.get());
    }

    private void freezeOrUnfreezeAccount(AccountDto accountDto) {
        String userChoice = "";

        while (true) {
            if (accountDto.isActive()) {
                System.out.println("Account number, " + accountDto.getNumber() + ", is not frozen.");
                System.out.println("Press F to freeze it, or Q to go back to main menu");
                userChoice = getUserInputString();
                if (userChoice.equals("F")) {
                    accountService.freezeAccount(accountDto.getNumber());
                    System.out.println("Account number, " + accountDto.getNumber() + ", is successfully frozen.");
                    return;
                }
            } else {
                System.out.println("Account number, " + accountDto.getNumber() + ", is frozen.");
                System.out.println("Press U to freeze it, or Q to go back to main menu");
                userChoice = getUserInputString();
                if (userChoice.equals("U")) {
                    accountService.unfreezeAccount(accountDto.getNumber());
                    System.out.println("Account number, " + accountDto.getNumber() + ", is successfully unfrozen.");
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
