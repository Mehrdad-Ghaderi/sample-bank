package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.Currency;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.exception.AccountNotFoundException;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import com.mehrdad.sample.bank.core.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 4/25/2025
 * Time: 12:59 AM
 */

@Component
@RequiredArgsConstructor
public class UserInterface {

    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final Utility utility;
//    private final DataSourceTransactionManager dataSourceTransactionManager;


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
                //"Enter 17 to see all the clients who have more than 10 dollars in their account\n" +
                "Any other number to Go Back");
    }

    public void start(String[] args) {


        while (true) {
            int userInput;

            printMenu();
            userInput = utility.getUserInputInt();
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
                continue;
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
            if (userInput == 11) {
                depositMoney();
                continue;
            }
            if (userInput == 12) {
                withdrawMoney();
                continue;
            }
            if (userInput == 13) {
                transferMoney();
                continue;
            }
            if (userInput == 14) {
                viewTransaction();
                continue;
            }
            if (userInput == 15) {
                viewAccountBalance();
                continue;
            }
            if (userInput == 16) {
                viewBankAccountBalance();
                continue;
            }
            if (userInput == 0) {
                System.out.println("The application was shut down by the user!");
                break;
            }
        }
    }

    protected void addNewClient() {
        System.out.println("New Client:");
        System.out.println("Enter the ID: ");
        String id = utility.getUserInputString();

        ClientDto client = clientService.getClientById(id);

        if (client != null) {
            System.out.println("The client is already a client of this bank.");

            if (client.getStatus() == Status.INACTIVE) {
                activateOrDeactivateClient(client);
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
                .status(Status.ACTIVE)
                .build();
        //ClientDto newClient = new ClientDto(id, name, phoneNumber, true);
        clientService.createClient(newClient);
        System.out.println(newClient.getName() + " was added to the repository.");

        //made it static so it can also be accessed from ClientMenu but it didn't work
        //AccountMenu.createAccountFor(newClient);
    }

    protected void updatePhoneNumber() {
        System.out.println("CLIENT UPDATE:");
        System.out.println("Enter the ID of the client whose phone number you would like to update:");
        String id = utility.getUserInputString();

        ClientDto foundClient = clientService.getClientById(id);
        if (foundClient == null) {
            System.out.println("The client does not exist.");
            return;
        }

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

        ClientDto foundClient = clientService.getClientById(id);
        if (foundClient == null) {
            System.out.println("No client with that ID was found.");
            return;
        }

        if (foundClient.getStatus() == Status.INACTIVE) {
            System.out.println(foundClient.getName() + "'s membership status is already inactive.");
            return;
        }

        clientService.deactivateClient(foundClient);
        System.out.println(foundClient.getName() + "'s membership status has been deactivated.");
    }

    protected void printClient() {
        System.out.println("Enter the ID of the client you want information on:");
        String clientId = utility.getUserInputString();
        ClientDto foundClient = clientService.getClientById(clientId);
        if (foundClient != null) {
            System.out.println(foundClient);
        } else {
            System.out.println("No client with that ID was found");

        }
    }

    protected void printAllClients() {
        List<ClientDto> allClients = clientService.getAllClients()
                .peek(System.out::println)
                .toList();

        String clients = (allClients.size() > 2) ? "s were" : " was";
        System.out.println(allClients.size() - 1 + " client" +
                clients
                + " found!");
    }

    protected void activateOrDeactivateClient() {
        System.out.println("Enter the client ID:");
        String id = utility.getUserInputString();
        ClientDto foundClient = clientService.getClientById(id);
        if (foundClient == null) {
            System.out.println("No client with that ID was found.");
            return;
        }
        activateOrDeactivateClient(foundClient);
    }

    protected void activateOrDeactivateClient(ClientDto client) {
        String userChoice = "";

        while (true) {
            if (client.getStatus() == Status.INACTIVE) {
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

    private void createAccount() {
        System.out.println("Account Setup:\n" +
                "Enter the ID of the client for whom you would like to open an account:");
        String clientID = utility.getUserInputString();
        ClientDto foundClient = clientService.getClientById(clientID);
        if (foundClient == null) {
            System.out.println("No client with the ID, " + clientID + ", was found.");
            return;
        }

        createAccountFor(foundClient);
    }

    protected void createAccountFor(ClientDto client) {

        List<AccountDto> accounts = ofNullable(client.getAccounts()).orElseGet(Collections::emptyList);

        if (accounts.isEmpty()) {
            System.out.printf("No account has been previously allocated to %s%n", client.getName());
        } else {
            System.out.printf("Here is a list of all available accounts of %s:%s%n", client.getName(), accounts);
        }

        var accountDto = new AccountDto();

        while (true) {
            System.out.println("Enter an account number to allocate to the client: account number + .1" +
                    "example: 111.1 or 111.2");
            String accountNumber = utility.getUserInputString();
            if (accounts.stream().anyMatch(account -> account.getNumber().equals(accountNumber))) {
                System.out.println("Account number " + accountNumber + " has already been allocated to " + client.getName() + ".");
                continue;
            }

            accountDto.setNumber(accountNumber);
            if (accountService.createAccount(accountDto, client)) {
                System.out.println("Account number " + accountNumber + " was allocated to " + client.getName() + " .");
            }
            break;
        }
    }

    protected void printAccount() {
        System.out.println("Enter the account number you want information on:");
        Optional<AccountDto> account = getAccountByAccountNumber();
        account.ifPresentOrElse(System.out::println, () -> System.out.println("No account with that number was found."));
    }

    protected void printAllAccounts() {
        accountService.getAllAccounts().forEach(System.out::println);
    }

    protected void freezeOrUnfreezeAccount() {
        System.out.println("Enter the account number you would like to freeze or unfreeze:");
        Optional<AccountDto> account = getAccountByAccountNumber();
        if (account.isEmpty()) return;
        freezeOrUnfreezeAccount(account.get());
    }

    protected void freezeOrUnfreezeAccount(AccountDto accountDto) {
        String userChoice = "";

        while (true) {
            if (accountDto.getActive()) {
                System.out.println("Account number, " + accountDto.getNumber() + ", is not frozen.");
                System.out.println("Press F to freeze it, or Q to go back to main menu");
                userChoice = utility.getUserInputString();
                if (userChoice.equals("F")) {
                    accountService.freezeAccount(accountDto.getNumber());
                    System.out.println("Account number, " + accountDto.getNumber() + ", is successfully frozen.");
                    return;
                }
            } else {
                System.out.println("Account number, " + accountDto.getNumber() + ", is frozen.");
                System.out.println("Press U to unfreeze it, or Q to go back to main menu");
                userChoice = utility.getUserInputString();
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

    protected void depositMoney() {
        System.out.println("Enter the account number you would like to deposit money into:");
        Optional<AccountDto> foundAccount = getAccountByAccountNumber();
        if (foundAccount.isEmpty()) return;

        AccountDto accountDto = foundAccount.get();
        if (accountInactive(accountDto)) {
            return;
        }

        MoneyDto money = createMoney(accountDto);

        boolean deposit = false;
        try {
            deposit = transactionService.deposit(accountDto, money, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printTransactionLog(accountDto, money, "deposited into ", deposit);
    }

    protected void withdrawMoney() {
        System.out.println("Enter the account number you would like to withdraw money from:");
        Optional<AccountDto> foundAccount = getAccountByAccountNumber();
        if (foundAccount.isEmpty()) return;

        AccountDto accountDto = foundAccount.get();
        if (accountInactive(accountDto)) {
            return;
        }

        MoneyDto money = createMoney(accountDto);

        try {
            transactionService.withdraw(accountDto, money, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printTransactionLog(accountDto, money, "withdrawn from", true);
    }

    private Optional<MoneyDto> prepareMoneyForTransaction() {
        Optional<AccountDto> foundAccount = getAccountByAccountNumber();
        if (foundAccount.isEmpty()) return Optional.empty();

        AccountDto accountDto = foundAccount.get();
        if (accountInactive(accountDto)) return Optional.empty();

        MoneyDto money = createMoney(accountDto);
        return Optional.of(money);
    }

    protected void transferMoney() {
        System.out.println("Enter the account number you would like to send money from:");
        Optional<AccountDto> foundSenderAccount = getAccountByAccountNumber();
        if (foundSenderAccount.isEmpty()) return;
        AccountDto senderAccount = foundSenderAccount.get();
        if (accountInactive(senderAccount)) {
            return;
        }

        System.out.println("Enter the account number you would like to send money to:");
        Optional<AccountDto> foundReceiverAccount = getAccountByAccountNumber();
        if (foundReceiverAccount.isEmpty()) return;
        if (accountInactive(senderAccount)) {
            return;
        }

        MoneyDto money = createMoney(senderAccount);
        boolean transaction = false;
        try {
            transaction = transactionService.transfer(senderAccount, foundReceiverAccount.get(), money);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        printTransactionLog(foundReceiverAccount.get(), money, "transferred from " + senderAccount.getNumber() + " to", transaction);
    }

    protected void viewTransaction() {
        System.out.println("Enter the account number:");
        Optional<AccountDto> foundAccount = getAccountByAccountNumber();
        if (foundAccount.isEmpty()) return;

        System.out.println("Enter the number of last transactions you would like to view:");
        int numOfLatestTransactions = utility.getUserInputInt();

        List<TransactionDto> lastTransactions = transactionService.getLastTransactions(foundAccount.get(), numOfLatestTransactions);
        if (lastTransactions.isEmpty()) {
            System.err.println("Account " + foundAccount.get().getNumber() + " has no transactions");
        } else {
            lastTransactions.forEach(System.out::println);
        }
    }

    protected void viewAccountBalance() {
        System.out.println("Enter the account number:");
        Optional<AccountDto> foundAccount = getAccountByAccountNumber();
        if (foundAccount.isEmpty()) return;

        System.out.println("Enter the currency:");
        Currency currency = getCurrency();

        List<MoneyDto> moneys = foundAccount.get().getMoneys();
        moneys.stream().filter(moneyDto -> moneyDto.getCurrency().equals(currency)).forEach(System.out::println);
    }


    protected boolean accountInactive(AccountDto account) {
        if (!account.getActive()) {
            System.out.println("Account number " + account.getNumber() + " is inactive.");
            return true;
        }
        return false;
    }

    protected MoneyDto createMoney(AccountDto account) {
        System.out.println("Enter the currency:");
        Currency currency = getCurrency();
        while (true) {
            System.out.println("Enter the amount:");
            BigDecimal amount = utility.getUserBigDecimal();
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                return new MoneyDto(currency, amount, account);
            } else {
                System.out.println("Negative amounts cannot be deposited");
            }
        }
    }

    protected Currency getCurrency() {
        while (true) {
            String currency = utility.getUserInputString();
            switch (currency) {
                case "USD":
                    return Currency.USD;
                case "CAD":
                    return Currency.CAD;
                case "EURO":
                    return Currency.EURO;
                case "RIAL":
                    return Currency.RIAL;
            }
            System.out.println("The bank does not support " + currency + " currency. \nTry another currency:");
        }
    }

    protected Optional<AccountDto> getAccountByAccountNumber() {
        String accountNumber = utility.getUserInputString();
        AccountDto account = null;
        try {
            account = accountService.getAccountByAccountNumber(accountNumber);
            return Optional.of(account);
        } catch (AccountNotFoundException e) {
            System.out.println("Account number " + accountNumber + " was NOT found.");
            return Optional.empty();
        }

    }

    protected void printTransactionLog(AccountDto account, MoneyDto money, String string, boolean transactionIsDone) {
        if (transactionIsDone) {
            System.out.println(money + " was successfully " + string + " account number " + account.getNumber() + ".");
        } else {
            System.out.println("Transaction was not successful.");
        }
    }

    private void viewBankAccountBalance() {
        AccountDto bankAccount = accountService.getAccountByAccountNumber("111.1");
        if (bankAccount == null) {
            System.out.println("Bank account was not found.");
            return;
        }

        System.out.println("Available funds in the bank:");
        bankAccount.getMoneys().stream()
                .map(moneyDto -> moneyDto.getCurrency() + "" + moneyDto.getAmount().negate())
                .forEach(System.out::println);
    }

}
