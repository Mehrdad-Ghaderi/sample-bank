package com.mehrdad.sample.bank.controllers;

import com.mehrdad.sample.bank.core.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
@Controller
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/list")
    public String displayAccounts(Model model) {
        var allAccounts = accountService.getAllAccounts();
        model.addAttribute("allAccounts", allAccounts);
        return "accounts/account-list";
    }
}
