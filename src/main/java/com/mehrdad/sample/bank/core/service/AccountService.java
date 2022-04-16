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

@Service
public class AccountService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ClientMapper clientMapper;

    public AccountService(ClientRepository clientRepository, AccountRepository accountRepository,
                          AccountMapper accountMapper, ClientMapper clientMapper) {

        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.clientMapper = clientMapper;
    }

    public Optional<AccountDto> getAccountByAccountNumber(String accountNumber) {
        ClientDto clientDto = getClientByAccountNumber(accountNumber);
        return accountRepository.findById(accountNumber).map(accountEntity -> accountMapper.toAccountDto(accountEntity, clientDto));
    }

    private ClientDto getClientByAccountNumber(String accountNumber) {
        Optional<AccountEntity> accountEntity = accountRepository.findById(accountNumber);
        return accountEntity.map(AccountEntity::getClient).map(clientMapper::toClientDto).orElse(null);
    }

    public List<AccountDto> getAllAccounts() {

        return clientRepository.findAll().parallelStream()
                .filter(ClientEntity::isActive)
                .map(clientMapper::toClientDto)
                .map(ClientDto::getAccounts)
                .flatMap(Collection::parallelStream)
                .filter(AccountDto::isActive)
                .collect(Collectors.toList());

/*
        // Solution 2
        Set<String> processedClients = new HashSet<>();
        return accountRepository.findAll().stream()
                .filter(accountEntity -> !processedClients.contains(accountEntity.getClient().getId()))
                .peek(accountEntity -> processedClients.add(accountEntity.getClient().getId()))
                .map(accountEntity -> clientMapper.toClientDto(accountEntity.getClient()))
                .flatMap(clientDto -> clientDto.getAccounts().parallelStream())
                .collect(Collectors.toList());
*/

/*
                    ClientDto clientDto = clientMap.get(clientId);
                    if (clientDto == null) {
                         clientDto = clientMapper.toClientDto(accountEntity.getClient());
                         clientMap.put(clientId, clientDto);
                    }
                    ClientDto clientDto = clientMap.computeIfAbsent(clientId, clientID -> clientMapper.toClientDto(accountEntity.getClient())
*/

/*
        //Solution 4
        return accountEntities.parallelStream()
                .map(accountEntity -> accountMapper.toAccountDto(accountEntity, clientMapper.toClientDto(accountEntity.getClient())))
                .collect(Collectors.toList());
*/


/*
        // Solution 1
        return accountRepository.findAll().parallelStream()
                .map(accountEntity -> clientMapper.toClientDto(accountEntity.getClient()))
                .flatMap(clientDto -> clientDto.getAccounts().parallelStream())
                .distinct().collect(Collectors.toList());
*/
    }

    public void save(AccountDto account, ClientDto clientDto) {
        ClientEntity clientEntity = clientMapper.toClientEntity(clientDto);
        accountRepository.save(accountMapper.toAccountEntity(account, clientEntity));
    }

    public boolean createAccount(String accountNumber, ClientDto client) {
        AccountDto account = new AccountDto(accountNumber, client, true);
        try {
            save(account, client);
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

    private void freezeOrUnfreezeAccount(String accountNumber, Boolean b) {
        AccountEntity foundAccount = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        foundAccount.setActive(b);
        accountRepository.save(foundAccount);
    }

}
