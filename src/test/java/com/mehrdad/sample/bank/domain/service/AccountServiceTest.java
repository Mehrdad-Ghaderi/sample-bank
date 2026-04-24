package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.account.UpdateAccountStatusRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.exception.account.AccountStatusAlreadySetException;
import com.mehrdad.sample.bank.domain.mapper.AccountMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String OWNER_USERNAME = "user";
    private static final String OTHER_USERNAME = "other-user";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAccountsShouldSearchByTrimmedAccountNumber() {
        String accountNumber = "2026-101-000046-001";
        PageRequest pageable = PageRequest.of(0, 5);
        AccountEntity account = ownedAccount(OWNER_USERNAME);
        AccountResponse accountResponse = new AccountResponse();

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountRepository.searchAccountsByOwner(OWNER_USERNAME, accountNumber, pageable))
                .thenReturn(new PageImpl<>(List.of(account)));
        when(accountMapper.mapToAccountResponse(account)).thenReturn(accountResponse);

        var result = accountService.getAccounts(OWNER_USERNAME, "  " + accountNumber + "  ", pageable);

        assertEquals(List.of(accountResponse), result.getContent());
        verify(accountRepository).findByNumber(accountNumber);
        verify(accountRepository).searchAccountsByOwner(OWNER_USERNAME, accountNumber, pageable);
        verify(accountMapper).mapToAccountResponse(account);
    }

    @Test
    void getAccountsShouldTreatBlankNumberAsNoNumberFilter() {
        PageRequest pageable = PageRequest.of(0, 5);

        when(accountRepository.searchAccountsByOwner(OWNER_USERNAME, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        var result = accountService.getAccounts(OWNER_USERNAME, "   ", pageable);

        assertEquals(List.of(), result.getContent());
        verify(accountRepository).searchAccountsByOwner(OWNER_USERNAME, null, pageable);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getAccountsShouldRejectAccountOwnedByAnotherUser() {
        String accountNumber = "2026-101-000046-001";
        PageRequest pageable = PageRequest.of(0, 5);
        AccountEntity account = ownedAccount(OTHER_USERNAME);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> accountService.getAccounts(OWNER_USERNAME, accountNumber, pageable));

        verify(accountRepository).findByNumber(accountNumber);
        verify(accountRepository, never()).searchAccountsByOwner(any(), any(), any());
        verifyNoInteractions(accountMapper);
    }

    @Test
    void updateAccountStatusShouldChangeAccountStatusAndReturnmappedResponse() {
        UUID accountId = UUID.randomUUID();

        AccountEntity account = ownedAccount(OWNER_USERNAME);
        account.setId(accountId);
        account.setStatus(Status.ACTIVE);

        UpdateAccountStatusRequest updateAccountStatusRequest = new UpdateAccountStatusRequest(Status.SUSPENDED);

        AccountResponse mappedResponse = new AccountResponse();
        mappedResponse.setId(accountId);
        mappedResponse.setStatus(Status.SUSPENDED);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.mapToAccountResponse(account)).thenReturn(mappedResponse);

        AccountResponse result = accountService.updateAccountStatus(accountId, updateAccountStatusRequest, OWNER_USERNAME);

        assertEquals(Status.SUSPENDED, account.getStatus());
        assertEquals(Status.SUSPENDED, result.getStatus());
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(account);
        verify(accountMapper).mapToAccountResponse(account);
    }

    @Test
    void updateAccountStatusShouldThrowWhenStatusIsAlreadySet() {
        UUID accountId = UUID.randomUUID();

        AccountEntity account = ownedAccount(OWNER_USERNAME);
        account.setId(accountId);
        account.setStatus(Status.ACTIVE);

        UpdateAccountStatusRequest updateAccountStatusRequest = new UpdateAccountStatusRequest(Status.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(
                AccountStatusAlreadySetException.class,
                () -> accountService.updateAccountStatus(accountId, updateAccountStatusRequest, OWNER_USERNAME)
        );

        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(account);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void updateAccountStatusShouldThrowWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        UpdateAccountStatusRequest updateAccountStatusRequest = new UpdateAccountStatusRequest(Status.SUSPENDED);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.updateAccountStatus(accountId, updateAccountStatusRequest, OWNER_USERNAME)
        );

        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(any());
        verifyNoInteractions(accountMapper);
    }

    @Test
    void setAccountStatusShouldUpdateAndSaveAccount() {
        UUID accountId = UUID.randomUUID();
        AccountEntity account = ownedAccount(OWNER_USERNAME);
        account.setId(accountId);
        account.setStatus(Status.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.setAccountStatus(accountId, Status.FROZEN);

        assertEquals(Status.FROZEN, account.getStatus());
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(account);
    }

    @Test
    void getAccountByIdShouldRejectAccountOwnedByAnotherUser() {
        UUID accountId = UUID.randomUUID();
        AccountEntity account = ownedAccount(OTHER_USERNAME);
        account.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> accountService.getAccountById(accountId, OWNER_USERNAME));

        verify(accountRepository).findById(accountId);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getAccountByIdShouldReturnOwnedAccount() {
        UUID accountId = UUID.randomUUID();
        AccountEntity account = ownedAccount(OWNER_USERNAME);
        account.setId(accountId);
        AccountResponse dto = new AccountResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.mapToAccountResponse(account)).thenReturn(dto);

        AccountResponse result = accountService.getAccountById(accountId, OWNER_USERNAME);

        assertEquals(dto, result);
        verify(accountRepository).findById(accountId);
        verify(accountMapper).mapToAccountResponse(account);
    }

    private static AccountEntity ownedAccount(String ownerUsername) {
        CustomerEntity customer = new CustomerEntity();
        customer.setOwnerUser(user(ownerUsername));

        AccountEntity account = new AccountEntity();
        account.setCustomer(customer);
        return account;
    }

    private static UserEntity user(String username) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        return user;
    }
}
