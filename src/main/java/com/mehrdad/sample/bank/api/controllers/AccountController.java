package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.PageResponse;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.account.AccountStatusUpdateDto;
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
    public ResponseEntity<PageResponse<AccountDto>> getAccounts(
            @RequestParam(required = false) String number,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(PageResponse.from(accountService.getAccounts(authentication.getName(), number, pageable)));
    }

    @GetMapping(ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID accountId,
                                                     Authentication authentication) {
        AccountDto accountDto = accountService.getAccountById(accountId, authentication.getName());
        return ResponseEntity.ok(accountDto);
    }

    @PatchMapping(ACCOUNT_RESOURCE_PATH)
    public ResponseEntity<AccountDto> updateAccountStatus(@PathVariable UUID accountId,
                                                          @Valid @RequestBody AccountStatusUpdateDto statusUpdateDto,
                                                          Authentication authentication) {
        AccountDto accountDto = accountService.updateAccountStatus(accountId, statusUpdateDto, authentication.getName());

        return ResponseEntity.ok(accountDto);
    }
}
