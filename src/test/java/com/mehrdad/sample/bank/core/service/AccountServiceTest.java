package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 5/23/2025
 * Time: 10:32 PM
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAccountByAccountNumber() {
        String accountNumber = "ACC123";

        // Create mock AccountEntity and ClientEntity
        AccountEntity mockAccountEntity = new AccountEntity();
        mockAccountEntity.setNumber(accountNumber);
        ClientEntity mockClientEntity = new ClientEntity();
        mockAccountEntity.setClient(mockClientEntity);

        // Mock behavior: accountRepository.findById
        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(mockAccountEntity));

        // Mock behavior: clientMapper.toClientDto
        ClientDto mockClientDto = new ClientDto();
        when(clientMapper.toClientDto(mockClientEntity)).thenReturn(mockClientDto);

        // Mock behavior: accountMapper.toAccountDto
        AccountDto mockAccountDto = new AccountDto();
        when(accountMapper.toAccountDto(mockAccountEntity, mockClientDto)).thenReturn(mockAccountDto);

        // Act
        AccountDto result = accountService.getAccountByAccountNumber(accountNumber);

        // Assert
        assertNotNull(result);
        assertEquals(mockAccountDto, result);

        // Verify interaction
        verify(accountRepository, times(1)).findById(accountNumber);
        verify(clientMapper).toClientDto(mockClientEntity);
        verify(accountMapper).toAccountDto(mockAccountEntity, mockClientDto);
    }

    @Test
    void getAllAccounts() {
    }

    @Test
    void save() {
    }

    @Test
    void createAccount() {
    }

    @Test
    void freezeAccount() {
    }

    @Test
    void unfreezeAccount() {
    }

    @Test
    void freezeOrUnfreezeAccount() {
    }
}