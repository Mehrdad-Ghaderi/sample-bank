package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.StatusUpdateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.core.service.AccountService;
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
public class AccountController {

    public static final String API_V1 = "/api/v1";

    public static final String ACCOUNTS_PATH = API_V1 + "/accounts";
    public static final String ACCOUNTS_ID_PATH = ACCOUNTS_PATH + "/{id}";
    public static final String ACCOUNTS_NUMBER_PATH = ACCOUNTS_PATH + "/{accountNumber}";

    private final AccountService accountService;

    /**
     * get all accounts default page size 5
     */
    @GetMapping(ACCOUNTS_PATH)
    public ResponseEntity<Page<AccountDto>> getAccounts(
            @PageableDefault(size = 5, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(accountService.getAccounts(pageable));
    }

    @GetMapping(ACCOUNTS_ID_PATH)
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID id) {
        AccountDto accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    /**
     * Get a single account by its account number
     */
/*
    @GetMapping(ACCOUNTS_NUMBER_PATH)
    public ResponseEntity<AccountDto> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
*/

    /**
     * update the status of an account
     */
    @PatchMapping(ACCOUNTS_ID_PATH)
    public ResponseEntity<AccountDto> updateStatus(@PathVariable UUID id,
                             @RequestBody StatusUpdateDto statusUpdateDto) {
        AccountDto accountDto = accountService.updateStatus(id, statusUpdateDto);

        return ResponseEntity.ok(accountDto);
    }

}
