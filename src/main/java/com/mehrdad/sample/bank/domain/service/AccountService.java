package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.account.AccountStatusUpdateDto;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public Page<AccountDto> getAccounts(String ownerUsername, String number, Pageable pageable) {
        String normalizedAccountNumber = normalizeOptionalAccountNumber(number);

        if (normalizedAccountNumber != null) {
            validateAccountOwnership(loadAccountByNumber(normalizedAccountNumber), ownerUsername);
        }

        return accountRepository.searchAccountsByOwner(ownerUsername, normalizedAccountNumber, pageable)
                .map(accountMapper::toAccountDto);
    }

    @Transactional
    public AccountDto updateAccountStatus(UUID accountId, AccountStatusUpdateDto statusUpdateDto, String ownerUsername) {
        AccountEntity foundAccount = loadAccountById(accountId);
        validateAccountOwnership(foundAccount, ownerUsername);

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

    @Transactional
    public void setAccountStatus(UUID accountId, Status status) {
        AccountEntity foundAccount = loadAccountById(accountId);

        foundAccount.setStatus(status);
        accountRepository.save(foundAccount);
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(UUID id, String ownerUsername) {
        AccountEntity account = loadAccountById(id);
        validateAccountOwnership(account, ownerUsername);
        return accountMapper.toAccountDto(account);
    }

    private String normalizeOptionalAccountNumber(String number) {
        if (number == null || number.isBlank()) {
            return null;
        }
        return number.trim();
    }

    private AccountEntity loadAccountByNumber(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private void validateAccountOwnership(AccountEntity account, String ownerUsername) {
        if (!ownerUsername.equals(account.getCustomer().getOwnerUsername())) {
            throw new AccessDeniedException("Account does not belong to authenticated user");
        }
    }
}
