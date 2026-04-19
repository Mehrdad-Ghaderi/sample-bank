package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.StatusUpdateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.domain.service.AccountService;
import jakarta.validation.Valid;
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

    private static final String ACCOUNT_RESOURCE_PATH = "/{accountId}";

    private final AccountService accountService;

    /**
     * get all accounts default page size 5
     */
    @GetMapping
    public ResponseEntity<Page<AccountDto>> getAccounts(
            @PageableDefault(size = 5, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(accountService.getAccounts(pageable));
    }

    @GetMapping(ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID accountId) {
        AccountDto accountDto = accountService.getAccountById(accountId);
        return ResponseEntity.ok(accountDto);
    }

    /**
     * update the status of an account
     */
    @PatchMapping(ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<AccountDto> updateStatus(@PathVariable UUID accountId,
                                                   @Valid @RequestBody StatusUpdateDto statusUpdateDto) {
        AccountDto accountDto = accountService.updateStatus(accountId, statusUpdateDto);

        return ResponseEntity.ok(accountDto);
    }

}
