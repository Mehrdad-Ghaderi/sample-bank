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

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class Menu implements UIState{

    protected final Scanner scanner;
    protected final ClientService clientService;
    protected final AccountService accountService;
    protected final TransactionService transactionService;
    protected final BalanceVisitor balanceVisitor;
    protected HomePage homePage;


    public Menu( Scanner scanner, ClientService clientService, AccountService accountService, TransactionService transactionService, BalanceVisitor balanceVisitor) {
        this.scanner = scanner;
        this.clientService = clientService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.balanceVisitor = balanceVisitor;
        //this.homePage = homePage;
    }


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

    public void run() {
        int userInput;

        while (true) {
            printMenu();
            userInput = getUserInputInt();
            if (userInput == 1) {
                getAccountMenu();
                homePage.run();
                continue;
            }
            if (userInput == 2) {
                getBankMenu();
                homePage.run();
                continue;
            }
            if (userInput == 3) {
                getClientMenu();
                homePage.run();
                continue;
            }
            if (userInput == 0) {
                System.out.println("The app was Shut Down by the user");
                break;
            }
        }
    }


    @Override
    public void runHomeMenu() {
        run();
    }

    @Override
    public void getAccountMenu() {
        homePage.setHomeMenu(homePage.getAccountMenu());
    }

    @Override
    public void getBankMenu() {
        homePage.setHomeMenu(homePage.getBankMenu());
    }

    @Override
    public void getClientMenu() {
        homePage.setHomeMenu(homePage.getClientMenu());
    }

    protected BigDecimal getUserBigDecimal() {
        while (true) {
            try {
                return scanner.nextBigDecimal();
            } catch (InputMismatchException e) {
                System.out.println("Please enter ONLY numbers.\nTry again:");
                scanner.nextLine();
            }
        }
    }

    protected String getUserInputString() {
        while (true) {
            try {
                return scanner.next().toUpperCase();
            } catch (NoSuchElementException e) {
                System.err.println("Unsupported characters.\n Try again:");
                scanner.nextLine();
            }
        }
    }

    protected int getUserInputInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter ONLY digits.\nTry again:");
                scanner.nextLine();
            }
        }
    }
}
