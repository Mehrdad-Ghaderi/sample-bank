package com.mehrdad.sample.bank.controllers;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller("/clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    @GetMapping("/list")
    public String DisplayClients(Model model) {

        var allClients = clientService.getAllClients().collect(Collectors.toList());
        model.addAttribute("allClients", allClients);
        return "clients/clients-list";
    }
}
