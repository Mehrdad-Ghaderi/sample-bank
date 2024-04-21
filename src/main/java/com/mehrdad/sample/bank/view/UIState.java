package com.mehrdad.sample.bank.view;

public interface UIState {

    void runHomeMenu();
    void getAccountMenu();
    void getBankMenu();
    void getClientMenu();

    void setHomePage(HomePage homePage);

}
