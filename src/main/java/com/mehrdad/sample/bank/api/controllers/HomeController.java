package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mehrdad Ghaderi
 */
@RestController
public class HomeController {

    @Autowired
    CustomerService customerService;
    @Autowired
    AccountService accountService;

    @GetMapping("/")
    public String hello() {
        return "Hello from cloud!";
    }

    @GetMapping("/home")
    public String displayHome(Model model) {

        long customersCount = customerService.getAllCustomers().count();
        model.addAttribute("customersCount", customersCount);

        long accountsCount = accountService.getAllAccounts().stream().count();
        model.addAttribute("accountsCount", accountsCount);

        return "main/home";
    }
}
