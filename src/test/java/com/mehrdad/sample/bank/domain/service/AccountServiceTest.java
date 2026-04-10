package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.StatusUpdateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.exception.account.AccountStatusAlreadySetException;
import com.mehrdad.sample.bank.domain.mapper.AccountMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void updateStatusShouldChangeAccountStatusAndReturnMappedDto() {
        UUID accountId = UUID.randomUUID();

        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        account.setStatus(Status.ACTIVE);

        StatusUpdateDto statusUpdateDto = new StatusUpdateDto(Status.SUSPENDED);

        AccountDto mappedDto = new AccountDto();
        mappedDto.setId(accountId);
        mappedDto.setStatus(Status.SUSPENDED);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account)).thenReturn(mappedDto);

        AccountDto result = accountService.updateStatus(accountId, statusUpdateDto);

        assertEquals(Status.SUSPENDED, account.getStatus());
        assertEquals(Status.SUSPENDED, result.getStatus());
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(account);
        verify(accountMapper).toAccountDto(account);
    }

    @Test
    void updateStatusShouldThrowWhenStatusIsAlreadySet() {
        UUID accountId = UUID.randomUUID();

        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        account.setStatus(Status.ACTIVE);

        StatusUpdateDto statusUpdateDto = new StatusUpdateDto(Status.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(
                AccountStatusAlreadySetException.class,
                () -> accountService.updateStatus(accountId, statusUpdateDto)
        );

        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(account);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void updateStatusShouldThrowWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        StatusUpdateDto statusUpdateDto = new StatusUpdateDto(Status.SUSPENDED);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.updateStatus(accountId, statusUpdateDto)
        );

        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(any());
        verifyNoInteractions(accountMapper);
    }

    @Test
    void setAccountStatusShouldUpdateAndSaveAccount() {
        UUID accountId = UUID.randomUUID();
        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        account.setStatus(Status.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.setAccountStatus(accountId, Status.FROZEN);

        assertEquals(Status.FROZEN, account.getStatus());
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(account);
    }
}
