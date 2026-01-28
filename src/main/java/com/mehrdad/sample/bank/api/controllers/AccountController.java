package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.core.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Created by Mehrdad Ghaderi
 */
@RestController
@RequiredArgsConstructor
public class AccountController {

    public static final String API_V1 = "/api/v1";

    public static final String ACCOUNTS_PATH = API_V1 + "/accounts";
    public static final String ACCOUNTS_ID_PATH = ACCOUNTS_PATH + "/{accountNumber}";

    private final AccountService accountService;

    @GetMapping(ACCOUNTS_PATH)
    public ResponseEntity<Page<AccountDto>> getAccounts(
            @PageableDefault(size = 5, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(accountService.getAccounts(pageable));
    }


    /**
     * Get a single account by its account number
     */
    @GetMapping(ACCOUNTS_ID_PATH)
    public ResponseEntity<AccountDto> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    /**
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
