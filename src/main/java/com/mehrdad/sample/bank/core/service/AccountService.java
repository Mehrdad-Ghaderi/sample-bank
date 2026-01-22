package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.CustomerDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.exception.AccountNotFoundException;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.CustomerMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
public class AccountService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CustomerMapper clientMapper;
    private final CustomerService clientService;

    public AccountService(CustomerRepository customerRepository, AccountRepository accountRepository,
                          AccountMapper accountMapper, CustomerMapper clientMapper, CustomerService clientService) {

        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.clientMapper = clientMapper;
        this.clientService = clientService;
    }

    public AccountDto getAccountByAccountNumber(String accountNumber){

        return accountRepository.findById(accountNumber)
                .map(accountMapper::toAccountDto)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public List<AccountDto> getAccountsByCustomerId(UUID clientId) {
        return clientService.getCustomerById(clientId).getAccounts();
    }

    public CustomerDto getCustomerByAccountNumber(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(AccountEntity::getCustomer)
                .map(clientMapper::toCustomerDto)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }


    public List<AccountDto> getAllAccounts() {

        return customerRepository.findAll().parallelStream()
                .filter(client -> client.getStatus() == Status.ACTIVE)
                .map(clientMapper::toCustomerDto)
                .map(CustomerDto::getAccounts)
                .flatMap(Collection::parallelStream)
                .filter(account -> account.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());
    }

    public void save(AccountDto account, CustomerDto customerDto) {
        accountRepository.save(accountMapper.toAccountEntity(account, clientMapper.toCustomerEntity(customerDto)));
    }

    public boolean createAccount(AccountDto account, CustomerDto customerDto) {
        try {
            account.setStatus(Status.ACTIVE);
            save(account, customerDto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void freezeAccount(String accountNumber) {
        freezeOrUnfreezeAccount(accountNumber, Status.FROZEN);
    }

    public void unfreezeAccount(String accountNumber) {
        freezeOrUnfreezeAccount(accountNumber, Status.ACTIVE);
    }

    public void freezeOrUnfreezeAccount(String accountNumber, Status status) {
        AccountEntity foundAccount = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        foundAccount.setStatus(status);
        accountRepository.save(foundAccount);
    }
}
