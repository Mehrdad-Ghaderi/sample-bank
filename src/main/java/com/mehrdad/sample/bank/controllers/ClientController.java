package com.mehrdad.sample.bank.controllers;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ClientController {

    public static final String CLIENT_PATH = "/api/v1/clients";
    public static final String CLIENT_PATH_ID = CLIENT_PATH + "/{clientId}";

    private final ClientService clientService;

    @GetMapping(CLIENT_PATH)
    public List<ClientDto> getAllClients() {

        return clientService.getAllClients().collect(Collectors.toList());
    }

    @GetMapping(CLIENT_PATH_ID)
    public ClientDto getClientById(@PathVariable("clientId") String clientId) {
        return clientService.getClientById(clientId).orElse(null);
    }

    @PostMapping(CLIENT_PATH)
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
        ClientDto savedClientDto = clientService.saveClient(clientDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedClientDto.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(CLIENT_PATH_ID)
    public void deleteClientById(@PathVariable("clientId") String clientId) {
        clientService.removeClientById(clientId);

    }

    /*@GetMapping("/list")
    public String DisplayClients(Model model) {

        var allClients = clientService.getAllClients().collect(Collectors.toList());
        model.addAttribute("allClients", allClients);
        return "clients/clients-list";
    }*/

    /*@GetMapping("/new")
    public String displayNewClientForm(Model model) {

        model.addAttribute("client", new ClientDto());
        return "/clients/client-new";
    }

    @PostMapping
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
    }*/
}
