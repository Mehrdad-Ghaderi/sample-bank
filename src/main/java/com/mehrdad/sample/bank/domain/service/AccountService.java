package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.StatusUpdateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.exception.account.AccountStatusAlreadySetException;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.mapper.AccountMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CustomerService clientService;


    public Page<AccountDto> getAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(accountMapper::toAccountDto);
    }

    @Transactional
    public AccountDto updateStatus(UUID id, StatusUpdateDto statusUpdateDto) {
        AccountEntity foundAccount = loadAccountById(id);

        Status newStatus = statusUpdateDto.getStatus();
        Status currentStatus = foundAccount.getStatus();
        if (currentStatus.equals(newStatus)) {
            throw new AccountStatusAlreadySetException(newStatus);
        }

        foundAccount.setStatus(newStatus);
        accountRepository.save(foundAccount);
        return accountMapper.toAccountDto(foundAccount);
    }

    private AccountEntity loadAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public void setAccountStatus(UUID accountId, Status status) {
        AccountEntity foundAccount = loadAccountById(accountId);

        foundAccount.setStatus(status);
        accountRepository.save(foundAccount);
    }

    public AccountDto getAccountByAccountNumber(String accountNumber){

        return accountRepository.findByNumber(accountNumber)
                .map(accountMapper::toAccountDto)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public List<AccountDto> getAccountsByCustomerId(UUID clientId) {
        return clientService.getCustomerById(clientId).getAccounts();
    }

    public AccountDto getAccountById(UUID id) {
        return accountRepository.findById(id)
                .map(accountMapper::toAccountDto)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
}
