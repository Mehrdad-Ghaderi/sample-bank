package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.exception.AccountNotFoundException;
import com.mehrdad.sample.bank.core.exception.CustomerNotFoundException;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.CustomerMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CustomerMapper clientMapper;
    private final CustomerService clientService;


    public Page<AccountDto> getAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(accountMapper::toAccountDto);
    }

    public AccountDto getAccountByAccountNumber(String accountNumber){

        return accountRepository.findByNumber(accountNumber)
                .map(accountMapper::toAccountDto)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public List<AccountDto> getAccountsByCustomerId(UUID clientId) {
        return clientService.getCustomerById(clientId).getAccounts();
    }

    public CustomerDto getCustomerByAccountNumber(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .map(AccountEntity::getCustomer)
                .map(clientMapper::toCustomerDto)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }


    public List<AccountDto> getAllAccounts() {

        return customerRepository.findAll().stream()
                .map(clientMapper::toCustomerDto)
                .map(CustomerDto::getAccounts)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void freezeAccount(String accountNumber) {
        freezeOrUnfreezeAccount(accountNumber, Status.FROZEN);
    }

    public void unfreezeAccount(String accountNumber) {
        freezeOrUnfreezeAccount(accountNumber, Status.ACTIVE);
    }

    public void freezeOrUnfreezeAccount(String accountNumber, Status status) {
        AccountEntity foundAccount = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        foundAccount.setStatus(status);
        accountRepository.save(foundAccount);
    }
}
