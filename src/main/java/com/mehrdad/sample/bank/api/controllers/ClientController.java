package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@RestController
@RequiredArgsConstructor
public class ClientController {

    public static final String API_V1 = "/api/v1";

    public static final String CLIENTS = API_V1 + "/clients";
    public static final String CLIENT_By_ID = CLIENTS + "/{clientId}";

    private final ClientService clientService;

    @GetMapping(CLIENTS)
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients().collect(Collectors.toList());
    }

    @GetMapping(CLIENT_By_ID)
    public ClientDto getClientById(@PathVariable String clientId) {
        return clientService.getClientById(clientId);
    }

    @PostMapping(CLIENTS)
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
        ClientDto savedClientDto = clientService.createClient(clientDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedClientDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedClientDto);
    }

    @DeleteMapping(CLIENT_By_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClientById(@PathVariable String clientId) {
        clientService.DeactivateClientById(clientId);
    }

    @PostMapping(CLIENT_By_ID)
    public void activateClient(@PathVariable String clientId) {
        clientService.activateClient(clientId);
    }
}
