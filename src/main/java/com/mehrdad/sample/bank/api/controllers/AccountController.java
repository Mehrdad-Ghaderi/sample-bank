package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.PageResponse;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.api.dto.account.UpdateAccountStatusRequest;
import com.mehrdad.sample.bank.domain.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping
    public ResponseEntity<PageResponse<AccountResponse>> getAccounts(
            @RequestParam(required = false) String number,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(PageResponse.createFrom(accountService.getAccounts(authentication.getName(), number, pageable)));
    }

    @GetMapping(ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID accountId,
                                                     Authentication authentication) {
        AccountResponse accountResponse = accountService.getAccountById(accountId, authentication.getName());
        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping(ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<AccountResponse> updateAccountStatus(@PathVariable UUID accountId,
                                                          @Valid @RequestBody UpdateAccountStatusRequest updateAccountStatusRequest,
                                                          Authentication authentication) {
        AccountResponse accountResponse = accountService.updateAccountStatus(accountId, updateAccountStatusRequest, authentication.getName());

        return ResponseEntity.ok(accountResponse);
    }
}
