package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.visitor.BalanceVisitor;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import com.mehrdad.sample.bank.core.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class Utility {
    protected final Scanner scanner;
    protected final ClientService clientService;
    protected final AccountService accountService;
    protected final TransactionService transactionService;
    protected final BalanceVisitor balanceVisitor;


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
