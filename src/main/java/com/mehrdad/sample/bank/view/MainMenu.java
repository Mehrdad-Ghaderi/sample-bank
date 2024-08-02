/*
package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.visitor.BalanceVisitor;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import com.mehrdad.sample.bank.core.service.TransactionService;

import java.util.Scanner;

public abstract class Menu {
    protected final Scanner scanner;
    protected final ClientService clientService;
    protected final AccountService accountService;
    protected final TransactionService transactionService;

    protected final BalanceVisitor balanceVisitor;

    public Menu(Scanner scanner, ClientService clientService, AccountService accountService, TransactionService transactionService, BalanceVisitor balanceVisitor) {
        this.scanner = scanner;
        this.clientService = clientService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.balanceVisitor = balanceVisitor;
    }

    public abstract void printMenu();

}
*/
package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.visitor.BalanceVisitor;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import com.mehrdad.sample.bank.core.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MainMenu implements UIState {

    protected final BalanceVisitor balanceVisitor;

    protected HomePage homePage;

    private final BankMenu bankMenu;
    private final AccountMenu accountMenu;
    private final ClientMenu clientMenu;
    private final Utility utility;


    public void setHomePage(HomePage homePage) {
        this.homePage = homePage;
    }

    public void printMenu() {
        System.out.println("************************************************\n" +
                "HOME PAGE:\n" +
                "Enter 1 to go to Account Menu\n" +
                "Enter 2 to go to Bank Menu\n" +
                "Enter 3 to go to Client menu\n" +
                "Enter 0 to Shut Down the app");
    }

    public UIState run(UIState previousState) {
        int userInput;

        printMenu();
        userInput = utility.getUserInputInt();
        if (userInput == 1) {
            return accountMenu;
        }
        if (userInput == 2) {
            return bankMenu;
        }
        if (userInput == 3) {
            return clientMenu;
        }
        System.out.println("The app was Shut Down by the user");
        throw new IllegalArgumentException();
    }

}
