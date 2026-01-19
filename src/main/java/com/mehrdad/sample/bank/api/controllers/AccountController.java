package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.core.service.AccountService;
import com.mehrdad.sample.bank.core.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


/**
 * Created by Mehrdad Ghaderi
 */
@RestController
@RequiredArgsConstructor
public class AccountController {

    public static final String API_V1 = "/api/v1";

    public static final String ACCOUNTS = API_V1 + "/accounts";
    public static final String ACCOUNT_BY_ID = ACCOUNTS + "/{accountNumber}";

    public static final String CLIENTS = API_V1 + "/clients";
    public static final String CLIENT_ACCOUNTS = CLIENTS + "/{clientId}/accounts";


    private final AccountService accountService;
    private final ClientService clientService;

    @GetMapping(ACCOUNTS)
    public List<AccountDto> getAccounts() {
        return accountService.getAllAccounts();
    }

    /**
     * Get a single account by its account number
     */
    @GetMapping(ACCOUNT_BY_ID)
    public ResponseEntity<AccountDto> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @GetMapping(CLIENT_ACCOUNTS)
    public ResponseEntity<List<AccountDto>> getAccountsByClientId(@PathVariable String clientId) {

        List<AccountDto> accounts = accountService.getAccountsByClientId(clientId);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get all accounts belonging to a client
     */
  /*  @PostMapping(CLIENT_ACCOUNTS)
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
    }*/


    /*
    *//**
     * Create a new account for a client
     *//*
    @PostMapping(CLIENT_ACCOUNTS)
    public ResponseEntity<AccountDto> createAccountForClient(
            @PathVariable String clientId,
            @RequestBody @Valid CreateAccountRequest request) {

        AccountDto created = accountService.createAccountForClient(clientId, request);

        URI location = URI.create(
                "/api/v1/accounts/" + created.getAccountNumber()
        );

        return ResponseEntity
                .created(location)
                .body(created);
    }

    *//**
     * Freeze an account
     *//*
    @PatchMapping(ACCOUNT_BY_ID + "/freeze")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void freezeAccount(@PathVariable String accountNumber) {
        accountService.freezeAccount(accountNumber);
    }

    *//**
     * Unfreeze an account
     *//*
    @PatchMapping(ACCOUNT_BY_ID + "/unfreeze")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfreezeAccount(@PathVariable String accountNumber) {
        accountService.unfreezeAccount(accountNumber);
    }

    *//**
     * Close an account permanently
     *//*
    @DeleteMapping(ACCOUNT_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAccount(@PathVariable String accountNumber) {
        accountService.closeAccount(accountNumber);
    }
    */
}
