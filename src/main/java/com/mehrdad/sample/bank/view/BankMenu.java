package com.mehrdad.sample.bank.view;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created by Mehrdad Ghaderi
 */
@Component
@RequiredArgsConstructor
public class BankMenu implements UIState {

    private final Utility utility;
    private final ClientService clientService;

    public void printMenu() {
        System.out.println("Bank Menu:\n" +
                "************************************************\n" +
                "Enter 1 to view the balance of the bank.\n" +

                "Enter 4 to see all the clients who have more than 10 dollars in their account\n" +
                "Any other number to Go Back.\n");

    }

    @Override
    public UIState run(UIState previousState) {
        int userInput;
        while (true) {
            printMenu();
            userInput = utility.getUserInputInt();
            if (userInput == 1) {
                viewBankAccountBalance();
            }

            System.out.println("Back to Manin Menu:");
            return previousState;
        }
    }
    

    protected void viewBankAccountBalance() {
        AccountDto bankAccount = utility.accountService.getAccountByAccountNumber("111.1");
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
