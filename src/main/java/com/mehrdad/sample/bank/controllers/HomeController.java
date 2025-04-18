package com.mehrdad.sample.bank.controllers;

import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by Mehrdad Ghaderi
 */
@Controller
public class HomeController {

    @Autowired
    ClientService clientService;
    @Autowired
    AccountService accountService;

    @GetMapping("/home")
    public String displayHome(Model model) {

        long clientsCount = clientService.getAllClients().count();
        model.addAttribute("clientsCount", clientsCount);

        long accountsCount = accountService.getAllAccounts().stream().count();
        model.addAttribute("accountsCount", accountsCount);

        return "main/home";
    }
}
