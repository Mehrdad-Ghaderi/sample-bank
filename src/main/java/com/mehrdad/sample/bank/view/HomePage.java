package com.mehrdad.sample.bank.view;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HomePage {

    UIState accountMenu;
    UIState bankMenu;
    UIState clientMenu;

    UIState homeMenu;

    public HomePage(Menu menu, AccountMenu accountMenu, BankMenu bankMenu, ClientMenu clientMenu) {
        this.accountMenu = accountMenu;
        this.bankMenu = bankMenu;
        this.clientMenu = clientMenu;

        homeMenu = menu;
    }

    @PostConstruct
    public void init() {
        homeMenu.setHomePage(this);
    }
    public void setHomeMenu(UIState newMenu) {
        this.homeMenu = newMenu;
    }
    public UIState getAccountMenu() {
        return accountMenu;
    }
    public UIState getBankMenu() {
        return bankMenu;
    }
    public UIState getClientMenu() {
        return clientMenu;
    }
    public UIState getHomeMenu() {
        return homeMenu;
    }

    public void run() {
        homeMenu.runHomeMenu();
    }

    /*public void accountMenu() {
        homeMenu.accountMenu();
    }
    public void bankMenu() {
        homeMenu.bankMenu();
    }
    public void clientMenu() {
        homeMenu.clientMenu();
    }*/


}
