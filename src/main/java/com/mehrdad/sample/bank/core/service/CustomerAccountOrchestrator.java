package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.core.entity.Status;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerAccountOrchestrator {

    private final CustomerService customerService;
    private final AccountService accountService;

    @Transactional
    public void updateCustomerAndAccountsStatus(UUID customerId, Status status) {
        if (status == Status.ACTIVE) {
            customerService.activateCustomer(customerId);
        } else {
            customerService.deactivateCustomer(customerId);
        }
        List<AccountDto> accounts = accountService.getAccountsByCustomerId(customerId);
        for (AccountDto accountDto : accounts) {
            accountService.setAccountStatus(accountDto.getId(), status);
        }
    }
}