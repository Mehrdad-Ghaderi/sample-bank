package com.mehrdad.sample.bank.view;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created by Mehrdad Ghaderi
 */
@Component
@RequiredArgsConstructor
public class MainMenu implements UIState {


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
