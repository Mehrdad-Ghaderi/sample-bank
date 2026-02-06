package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAccountOrchestrator {

    private final CustomerRepository customerService;
    private final AccountRepository accountService;

    public void saveAccountOFCustomer(CustomerEntity customerEntity, AccountEntity accountEntity) {
        accountService.save(accountEntity);
    }
}