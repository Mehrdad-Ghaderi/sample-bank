package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.StatusUpdateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.domain.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


/**
 * Created by Mehrdad Ghaderi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_BASE_PATH + ApiPaths.ACCOUNTS)
public class AccountController {

    public static final String ACCOUNT_ID_PATH = ApiPaths.API_BASE_PATH + ApiPaths.ACCOUNTS + "/{id}";
    public static final String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
    private static final String ID_PATH = "/{id}";

    private final AccountService accountService;

    /**
     * get all accounts default page size 5
     */
    @GetMapping
    public ResponseEntity<Page<AccountDto>> getAccounts(
            @PageableDefault(size = 5, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(accountService.getAccounts(pageable));
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID id) {
        AccountDto accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    /**
     * Get a single account by its account number
     */
/*
    @GetMapping(ACCOUNT_NUMBER_PATH)
    public ResponseEntity<AccountDto> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
*/

    /**
     * update the status of an account
     */
    @PatchMapping(ID_PATH)
    public ResponseEntity<AccountDto> updateStatus(@PathVariable UUID id,
                             @RequestBody StatusUpdateDto statusUpdateDto) {
        AccountDto accountDto = accountService.updateStatus(id, statusUpdateDto);

        return ResponseEntity.ok(accountDto);
    }

}
