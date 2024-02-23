package com.mehrdad.sample.bank.controllers;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    @GetMapping("/list")
    public String DisplayClients(Model model) {

        var allClients = clientService.getAllClients().collect(Collectors.toList());
        model.addAttribute("allClients", allClients);
        return "clients/clients-list";
    }

    @GetMapping("/new")
    public String displayNewClientForm(Model model) {
        model.addAttribute("client", new ClientDto());
        return "/clients/client-new";
    }

    @PostMapping("/save")
    public String createClient(ClientDto clientDto, Model model) {
        clientDto.setActive(true);
        clientService.saveClient(clientDto);
        return "redirect:/clients/list";
    }
}
