package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.core.entity.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerAccountOrchestrator {

    private final CustomerService customerService;
    private final AccountService accountService;

    public CustomerAccountOrchestrator(CustomerService customerService,
                                       AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
    }

    @Transactional
    public void updateCustomerAndAccountsStatus(UUID customerId, Status status) {
        if (status == Status.ACTIVE) {
            customerService.activateCustomer(customerId);
        } else {
            customerService.deactivateCustomer(customerId);
        }
        List<AccountDto> accounts = accountService.getAccountsByCustomerId(customerId);
        for (AccountDto accountDto : accounts) {
            accountService.freezeOrUnfreezeAccount(accountDto.getNumber(), status);
        }
    }
}