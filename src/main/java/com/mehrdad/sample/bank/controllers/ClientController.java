package com.mehrdad.sample.bank.controllers;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Slf4j
@RequiredArgsConstructor
@RestController
//@RequestMapping(ClientController.CLIENT_PATH)
public class ClientController {

    public static final String CLIENT_PATH = "/api/v1/client";
    public static final String CLIENT_PATH_ID = CLIENT_PATH + "/{clientId}";

    private final ClientService clientService;

    @GetMapping("/list")
    public String DisplayClients(Model model) {

        var allClients = clientService.getAllClients().collect(Collectors.toList());
        model.addAttribute("allClients", allClients);
        return "clients/clients-list";
    }

    @GetMapping(CLIENT_PATH)
    public List<ClientDto> getAllClients() {

        return clientService.getAllClients().collect(Collectors.toList());
    }

    @GetMapping("/new")
    public String displayNewClientForm(Model model) {
        model.addAttribute("client", new ClientDto());
        return "/clients/client-new";
    }

    @PostMapping("/save")
    public String createClient(ClientDto clientDto, Model model) {
        var clientById = clientService.getClientById(clientDto.getId());
        if (clientById.isPresent()) {

            return "redirect:/clients/failed-submission";
        }
        clientDto.setActive(true);
        clientService.saveClient(clientDto);
        return "/common/successful-submission";
    }

    @GetMapping("/failed-submission")
    public String failedSubmission(Model model) {
        return "/common/failed-submission";
    }

    @GetMapping("/successful-submission")
    public String successfulSubmission(Model model) {
        return "/common/successful-submission";
    }
}
