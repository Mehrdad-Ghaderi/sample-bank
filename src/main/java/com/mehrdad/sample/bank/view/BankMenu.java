package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.api.dto.textservice.Event;
import com.mehrdad.sample.bank.api.dto.visitor.BalanceVisitor;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import com.mehrdad.sample.bank.core.service.TransactionService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class BankMenu extends Menu {

    public BankMenu(Scanner scanner, ClientService clientService, AccountService accountService, TransactionService transactionService, BalanceVisitor balanceVisitor) {
        super(scanner, clientService, accountService, transactionService, balanceVisitor);
    }


    @Override
    public void printMenu() {
        System.out.println("Bank Menu:\n" +
                "************************************************\n" +
                "Enter 1 to view the balance of the bank.\n" +
                "Enter 2 to send active members the newsletter\n" +
                "Enter 3 to subscribe to the bank's newsletter\n" +
                "Enter 4 to see all the clients who have more than 10 dollars in their account\n" +
                "Enter 0 to Go Back.\n");

    }

    @Override
    public void run() {
        int userInput;
        while (true) {
            printMenu();
            userInput = getUserInputInt();
            if (userInput == 1) {
                viewBankAccountBalance();
                continue;
            }
            if (userInput == 2) {
                sendNewsletter();
                continue;
            }
            if (userInput == 3) {
                becomeSubscriber();
                continue;
            }
            if (userInput == 4) {
                showClientsWithMoreThan10Dollars();
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

    protected void viewBankAccountBalance() {
        AccountDto bankAccount = accountService.getAccountByAccountNumber("111.1");
        if (bankAccount == null) {
            System.out.println("Bank account was not found.");
            return;
        }

        System.out.println("Here is the list of all the currencies available in the bank:");
        bankAccount.getMoneys().stream()
                .map(moneyDto -> moneyDto.getAmount().negate())
                .forEach(System.out::println);
    }

    protected void sendNewsletter() {
        System.out.println("Type the message");
        String message = getUserInputString();
        clientService.notifyListeners(new Event(message));
    }

    protected void becomeSubscriber() {
        System.out.println("Enter a Client ID:");
        String id = getUserInputString();

        Optional<ClientDto> client = clientService.getClientById(id);

        if (client.isPresent()) {
            ClientDto foundClient = client.get();
            clientService.registerListener(foundClient);
        } else System.out.println("Client ID '" + id + "' was not found");
    }

    protected void showClientsWithMoreThan10Dollars() {
        var clients = clientService.accept(balanceVisitor);
        if (clients.isEmpty()) {
            System.out.println("No client has more than 10 dollars in the bank");
        } else {
            System.out.println(clients);
        }
    }
}
