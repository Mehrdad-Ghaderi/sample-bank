package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.mehrdad.sample.bank.api.controllers.AccountController.ACCOUNT_PATH;

/**
 * Created by Mehrdad Ghaderi
 */
@RestController(ACCOUNT_PATH)
@RequiredArgsConstructor
public class AccountController {

    public static final String ACCOUNT_PATH = "/api/v1/accounts";
    public static final String ACCOUNT_PATH_ID = ACCOUNT_PATH + "/{clientId}";

    private final AccountService accountService;
    private final ClientService clientService;

    @GetMapping("/list")
    public List<AccountDto> displayAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping(ACCOUNT_PATH_ID)
    public List<AccountDto> getAccountsByClientId(@PathVariable String clientId) {
        return clientService.getClientById(clientId).getAccounts();
    }

    @PostMapping(ACCOUNT_PATH_ID)
    public ResponseEntity<Object> createAccountByClientId(@PathVariable("clientId") String clientID,
                                                          @RequestBody AccountDto accountDto) {

        ClientDto foundClient = clientService.getClientById(clientID);

        accountService.createAccount(accountDto, foundClient);
        AccountDto savedAccountDto = accountService.getAccountByAccountNumber(accountDto.getNumber());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedAccountDto.getNumber())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
