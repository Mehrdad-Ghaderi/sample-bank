package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import com.mehrdad.sample.bank.domain.repository.CustomerRepository;
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