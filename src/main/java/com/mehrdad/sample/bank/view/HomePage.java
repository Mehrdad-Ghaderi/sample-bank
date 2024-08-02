package com.mehrdad.sample.bank.view;

import org.springframework.stereotype.Component;

@Component
public class HomePage {

    private UIState menu;

    public HomePage(MainMenu mainMenu) {
        this.menu = mainMenu;
    }

    public void run() {
        UIState previousMenu = menu;

        while (true) {
            this.menu = menu.run(previousMenu); // first is null cuz there is no previous state
        }
    }
}
