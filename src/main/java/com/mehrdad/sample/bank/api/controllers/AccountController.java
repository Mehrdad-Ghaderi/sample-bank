package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.core.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mehrdad.sample.bank.api.controllers.AccountController.ACCOUNT_PATH;

/**
 * Created by Mehrdad Ghaderi
 */
@RestController(ACCOUNT_PATH)
@RequiredArgsConstructor
public class AccountController {

    public static final String ACCOUNT_PATH = "accounts";

    private final AccountService accountService;

    @GetMapping("/list")
    public List<AccountDto> displayAccounts() {
        return accountService.getAllAccounts();
    }
}
