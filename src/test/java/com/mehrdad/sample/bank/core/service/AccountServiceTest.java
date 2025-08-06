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

import java.util.List;
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

        // Mock behavior: accountMapper.toAccountDto
        AccountDto mockAccountDto = new AccountDto();
        when(accountMapper.toAccountDto(mockAccountEntity)).thenReturn(mockAccountDto);

        // Act
        AccountDto result = accountService.getAccountByAccountNumber(accountNumber);

        // Assert
        assertNotNull(result);
        assertEquals(mockAccountDto, result);

        // Verify interaction
        verify(accountRepository, times(1)).findById(accountNumber);
        verify(accountMapper).toAccountDto(mockAccountEntity);
    }

    @Test
    void testGetAllAccounts_ReturnsActiveAccounts() {
        // Arrange
        ClientEntity client1 = new ClientEntity();
        client1.setActive(true);

        ClientEntity client2 = new ClientEntity();
        client2.setActive(false); // should be ignored

        List<ClientEntity> clientEntities = List.of(client1, client2);

        when(clientRepository.findAll()).thenReturn(clientEntities);

        // Create DTOs for active client
        ClientDto clientDto1 = new ClientDto();

        AccountDto activeAccount1 = new AccountDto();
        activeAccount1.setActive(true);

        AccountDto inactiveAccount = new AccountDto();
        inactiveAccount.setActive(false); // should be filtered out

        clientDto1.setAccounts(List.of(activeAccount1, inactiveAccount));

        when(clientMapper.toClientDto(client1)).thenReturn(clientDto1);

        // Act
        List<AccountDto> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());

        // Verify
        verify(clientRepository).findAll();
        verify(clientMapper).toClientDto(client1);
        verify(clientMapper, never()).toClientDto(client2); // client2 is inactive
    }


    @Test
    void save_shouldMapAndPersistAccount() {
        // Arrange
        AccountDto mockAccountDto = new AccountDto();
        ClientDto mockClientDto = new ClientDto();

        ClientEntity mockClientEntity = new ClientEntity();
        AccountEntity mockAccountEntity = new AccountEntity();

        when(accountMapper.toAccountEntity(mockAccountDto)).thenReturn(mockAccountEntity);

        // Act
        accountService.save(mockAccountDto, clientDto);

        // Assert
        verify(accountMapper).toAccountEntity(mockAccountDto);
        verify(accountRepository).save(mockAccountEntity);
    }


    @Test
    void createAccount_shouldReturnTrue_whenSaveIsSuccessful() {
        // Arrange
        AccountDto account = new AccountDto();
        ClientDto client = new ClientDto();

        // No need to stub anything; we just want save to run without error

        // Act
        boolean result = accountService.createAccount(account, client);

        // Assert
        assertTrue(result);
        assertTrue(account.getActive()); // check that account was set active
        verify(accountRepository).save(any()); // check that save was called
    }

    @Test
    void createAccount_shouldReturnFalse_whenSaveThrowsException() {
        // Arrange
        AccountDto account = new AccountDto();
        ClientDto client = new ClientDto();

        // Stub accountRepository.save() to throw exception
        doThrow(new RuntimeException("DB Error"))
                .when(accountRepository).save(any());

        // Act
        boolean result = accountService.createAccount(account, client);

        // Assert
        assertFalse(result);
        verify(accountRepository).save(any()); // still verify save was attempted
    }


    @Test
    void freezeAccount_shouldDeactivateAccount() {
        // Arrange
        String accountNumber = "ACC123";
        AccountEntity mockAccount = new AccountEntity();
        mockAccount.setNumber(accountNumber);
        mockAccount.setActive(true);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(mockAccount));

        // Act
        accountService.freezeAccount(accountNumber);

        // Assert
        assertFalse(mockAccount.getActive()); // account should be frozen
        verify(accountRepository).save(mockAccount); // ensure save was called
    }

    @Test
    void unfreezeAccount_shouldActivateAccount() {
        // Arrange
        String accountNumber = "ACC123";
        AccountEntity mockAccount = new AccountEntity();
        mockAccount.setNumber(accountNumber);
        mockAccount.setActive(false); // assume it starts frozen

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(mockAccount));

        // Act
        accountService.unfreezeAccount(accountNumber);

        // Assert
        assertTrue(mockAccount.getActive());
        verify(accountRepository).save(mockAccount); // ensure save was triggered
    }

}