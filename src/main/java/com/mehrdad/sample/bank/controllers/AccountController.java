package com.mehrdad.sample.bank.controllers;

import com.mehrdad.sample.bank.core.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mehrdad.sample.bank.controllers.AccountController.ACCOUNT_PATH;

/**
 * Created by Mehrdad Ghaderi
 */
@RestController(ACCOUNT_PATH)
public class AccountController {

    public static final String ACCOUNT_PATH = "/accounts";

    @Autowired
    AccountService accountService;

    @GetMapping("/list")
    public String displayAccounts(Model model) {
        var allAccounts = accountService.getAllAccounts();
        model.addAttribute("allAccounts", allAccounts);
        return "accounts/account-list";
    }
}
