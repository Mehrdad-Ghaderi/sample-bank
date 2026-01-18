package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ClientController {

    public static final String CLIENT_PATH = "api/v1/clients";
    public static final String CLIENT_PATH_ID = CLIENT_PATH + "/{clientId}";

    private final ClientService clientService;
    private final AccountService accountService;

    @GetMapping(CLIENT_PATH)
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients().collect(Collectors.toList());
    }

    @GetMapping(CLIENT_PATH_ID)
    public ClientDto getClientById(@PathVariable String clientId) {
        return clientService.getClientById(clientId);
    }

    @PostMapping(CLIENT_PATH)
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
        ClientDto savedClientDto = clientService.saveClient(clientDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedClientDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedClientDto);
    }

    @DeleteMapping(CLIENT_PATH_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClientById(@PathVariable String clientId) {
        clientService.removeClientById(clientId);
    }

    @PostMapping(CLIENT_PATH_ID)
    public void activateClient(@PathVariable String clientId) {
        clientService.activateClient(clientId);
    }
}
