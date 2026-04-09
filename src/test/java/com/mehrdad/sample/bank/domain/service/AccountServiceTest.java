package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.StatusUpdateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

        verify(accountRepository, never()).save(account);
        verify(accountMapper, never()).toAccountDto(account);
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

        verify(accountRepository, never()).save(any());
        verify(accountMapper, never()).toAccountDto(any());
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
        verify(accountRepository).save(account);
    }

    @Test
    void getAccountByAccountNumberShouldReturnMappedDto() {
        String accountNumber = "123456";
        AccountEntity account = new AccountEntity();
        account.setNumber(accountNumber);

        AccountDto mappedDto = new AccountDto();
        mappedDto.setNumber(accountNumber);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account)).thenReturn(mappedDto);

        AccountDto result = accountService.getAccountByAccountNumber(accountNumber);

        assertEquals(accountNumber, result.getNumber());
        verify(accountMapper).toAccountDto(account);
    }

    @Test
    void getAccountByAccountNumberShouldThrowWhenAccountDoesNotExist() {
        String accountNumber = "ACC-404";

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountByAccountNumber(accountNumber)
        );
    }

    @Test
    void getAccountsByCustomerIdShouldReturnAccountsFromCustomerService() {
        UUID customerId = UUID.randomUUID();
        AccountDto firstAccount = new AccountDto();
        AccountDto secondAccount = new AccountDto();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setAccounts(List.of(firstAccount, secondAccount));

        when(customerService.getCustomerById(customerId)).thenReturn(customerDto);

        List<AccountDto> result = accountService.getAccountsByCustomerId(customerId);

        assertEquals(2, result.size());
        assertEquals(List.of(firstAccount, secondAccount), result);
    }

    @Test
    void getAccountByIdShouldReturnMappedDto() {
        UUID accountId = UUID.randomUUID();
        AccountEntity account = new AccountEntity();
        account.setId(accountId);

        AccountDto mappedDto = new AccountDto();
        mappedDto.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account)).thenReturn(mappedDto);

        AccountDto result = accountService.getAccountById(accountId);

        assertEquals(accountId, result.getId());
        verify(accountMapper).toAccountDto(account);
    }

    @Test
    void getAccountByIdShouldThrowWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountById(accountId)
        );
    }
}
