package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.exception.AccountNotFoundException;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
public class AccountService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ClientMapper clientMapper;
    private final ClientService clientService;

    public AccountService(ClientRepository clientRepository, AccountRepository accountRepository,
                          AccountMapper accountMapper, ClientMapper clientMapper, ClientService clientService) {

        this.clientRepository = clientRepository;
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

    public List<AccountDto> getAccountsByClientId(String clientId) {
        return clientService.getClientById(clientId).getAccounts();
    }

    public ClientDto getClientByAccountNumber(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(AccountEntity::getClient)
                .map(clientMapper::toClientDto)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

//        Optional<AccountEntity> accountEntity = accountRepository.findById(accountNumber);
//        return accountEntity.map(AccountEntity::getClient).map(clientMapper::toClientDto).orElse(null);
    }


    public List<AccountDto> getAllAccounts() {

        return clientRepository.findAll().parallelStream()
                .filter(ClientEntity::getActive)
                .map(clientMapper::toClientDto)
                .map(ClientDto::getAccounts)
                .flatMap(Collection::parallelStream)
                .filter(AccountDto::getActive)
                .collect(Collectors.toList());
    }

    public void save(AccountDto account, ClientDto clientDto) {
        accountRepository.save(accountMapper.toAccountEntity(account, clientMapper.toClientEntity(clientDto)));
    }

    public boolean createAccount(AccountDto account, ClientDto clientDto) {
        try {
            account.setActive(true);
            save(account, clientDto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void freezeAccount(String accountNumber) {
        freezeOrUnfreezeAccount(accountNumber, false);
    }

    public void unfreezeAccount(String accountNumber) {
        freezeOrUnfreezeAccount(accountNumber, true);
    }

    public void freezeOrUnfreezeAccount(String accountNumber, Boolean activate) {
        AccountEntity foundAccount = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        foundAccount.setActive(activate);
        accountRepository.save(foundAccount);
    }
}
