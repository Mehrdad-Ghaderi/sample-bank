package com.mehrdad.sample.bank.controllers;

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
        return ResponseEntity.created(location).body(savedClientDto);
    }

    @DeleteMapping(CLIENT_PATH_ID)
    public void deleteClientById(@PathVariable("clientId") String clientId) {
        clientService.removeClientById(clientId);
    }

    @PostMapping(CLIENT_PATH_ID)
    public void activateClient(@PathVariable("clientId") String clientId) {
        clientService.activateClient(clientId);
    }

    @GetMapping(CLIENT_PATH_ID + "/accounts")
    public List<AccountDto> getAccountsByClientId(@PathVariable("clientId") String clientId) {
        return clientService.getClientById(clientId)
                .map(ClientDto::getAccounts)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }

    @PostMapping(CLIENT_PATH_ID + "/accounts")
    public ResponseEntity<Object> createAccountByClientId(@PathVariable("clientId") String clientID,
                                                          @RequestBody AccountDto accountDto) {

        Optional<ClientDto> foundClient = clientService.getClientById(clientID);

        if (foundClient.isEmpty()) {
            throw new ClientNotFoundException(clientID);
        }

        accountService.createAccount(accountDto, foundClient.get());
        AccountDto savedAccountDto = accountService.getAccountByAccountNumber(accountDto.getNumber());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedAccountDto.getNumber())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
