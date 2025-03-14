package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.Currency;
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
 * Created by Mehrdad Ghaderi
 */
@Component
@RequiredArgsConstructor
public class AccountMenu implements UIState {


    private final Utility utility;
    private final AccountService accountService;
    private final ClientService clientService;
    private final TransactionService transactionService;

    public void printMenu() {
        System.out.println("Account Menu:\n" +
                "************************************************\n" +
                "Enter 1 to create an account\n" +
                "Enter 2 to view information on an account:\n" +
                "Enter 3 to view all accounts in the bank:\n" +
                "Enter 4 to freeze or unfreeze an account:\n" +
                "Enter 5 to deposit money.\n" +
                "Enter 6 to withdraw money.\n" +
                "Enter 7 to transfer money.\n" +
                "Enter 8 to view the transactions of an account.\n" +
                "Enter 9 to view the balance of an account.\n" +
                "Enter 10 to view the balance of the bank.\n" +
                "Any other numbers to GO Back.\n"
        );
    }
    @Override
    public UIState run(UIState previousState) {

        printMenu();
        int userInput = utility.getUserInputInt();

        if (userInput == 1) {
            createAccount();
        }
        if (userInput == 2) {
            printAccount();
        }
        if (userInput == 3) {
            printAllAccounts();
        }
        if (userInput == 4) {
            freezeOrUnfreezeAccount();
        }
        if (userInput == 5) {
            depositMoney();
        }
        if (userInput == 6) {
            withdrawMoney();
        }
        if (userInput == 7) {
            transferMoney();
        }
        if (userInput == 8) {
            viewTransaction();
        }
        if (userInput == 9) {
            viewAccountBalance();
        }
        if (userInput == 10) {
            viewBankAccountBalance();
        }
        return previousState;
    }

    private void createAccount() {
        System.out.println("Account Setup:\n" +
                "Enter the ID of the client for whom you would like to open an account:");
        String clientID = utility.getUserInputString();
        Optional<ClientDto> foundClient = clientService.getClientById(clientID);
        if (foundClient.isEmpty()) {
            System.out.println("No client with the ID, " + clientID + ", was found.");
            return;
        }

        createAccountFor(foundClient.get());
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
            if (accountDto.isActive()) {
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
            deposit = transactionService.deposit(money, true);
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

        boolean withdraw = false;
        try {
            withdraw = transactionService.withdraw(money, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printTransactionLog(accountDto, money, "withdrawn from", withdraw);
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

        MoneyDto money = createMoney(senderAccount);

        boolean transaction = false;
        try {
            transaction = transactionService.transfer(senderAccount, foundReceiverAccount.get(), money);
        } catch (Exception e) {
            e.printStackTrace();
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
        if (lastTransactions == null) {
            System.out.println("Account " + foundAccount + " has no transactions");
        } else {
            lastTransactions.forEach(System.out::println);
        }
    }

    protected void viewAccountBalance() {
        System.out.println("BALANCE:" +
                "Enter the account number:");
        Optional<AccountDto> foundAccount = getAccountByAccountNumber();
        if (foundAccount.isEmpty()) return;

        System.out.println("Enter the currency:");
        Currency currency = getCurrency();

        List<MoneyDto> moneys = foundAccount.get().getMoneys();
        moneys.stream().filter(moneyDto -> moneyDto.getCurrency().equals(currency)).forEach(System.out::println);
    }


    protected boolean accountInactive(AccountDto account) {
        if (!account.isActive()) {
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
        } catch (Exception e) {
            throw new AccountNotFoundException(accountNumber);
        }
        if (account == null) {
            System.out.println("Account number, " + accountNumber + ", was NOT found.");
            return Optional.empty();
        }
        return Optional.of(account);
    }

    protected void printTransactionLog(AccountDto account, MoneyDto money, String string, boolean transactionIsDone) {
        if (transactionIsDone) {
            System.out.println(money.getAmount() + money.getCurrency().toString() + " was successfully " + string + " account number " + account.getNumber() + ".");
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

        System.out.println("Here is the list of all the currencies available in the bank:");
        bankAccount.getMoneys().stream()
                .map(moneyDto -> moneyDto.getAmount().negate())
                .forEach(System.out::println);
    }
}
