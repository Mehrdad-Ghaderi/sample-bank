package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import com.mehrdad.sample.bank.core.exception.MoneyNotFoundException;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.mapper.MoneyMapper;
import com.mehrdad.sample.bank.core.mapper.TransactionMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.MoneyRepository;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private MoneyRepository moneyRepository;
    @Mock
    private MoneyMapper moneyMapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    @Spy // Needed to mock internal method calls
    private TransactionService transactionService;

    @Test
    void transfer_shouldSucceed_whenWithdrawAndDepositSucceed() throws Exception {
        // Arrange

        AccountDto sender = new AccountDto();
        sender.setNumber("12345");
        AccountDto receiver = new AccountDto();
        receiver.setNumber("67890");
        MoneyDto money = mock(MoneyDto.class);
        money.setAccount(sender); // or receiver depending on context
        money.setAmount(BigDecimal.valueOf(100));

        // Mock internal methods
        doNothing().when(transactionService).withdraw(money, false);

        // Act
        boolean result = transactionService.transfer(sender, receiver, money);

        // Assert
        assertThat(result).isTrue();
        verify(transactionService).withdraw(money, false);
        verify(moneyRepository).save(any(MoneyEntity.class));   // from changeMoneyIdAndAccount
        verify(transactionService).deposit(money, false);
        verify(transactionRepository).save(any(TransactionEntity.class));  // from saveTransaction
    }

    @Test
    void transfer_shouldThrow_whenWithdrawFails() throws Exception {
        // Arrange
        AccountDto sender = mock(AccountDto.class);
        AccountDto receiver = mock(AccountDto.class);
        MoneyDto money = mock(MoneyDto.class);

        doThrow(new MoneyNotFoundException("Money not found"))
                .when(transactionService).withdraw(money, false);

        // Act + Assert
        assertThatThrownBy(() -> transactionService.transfer(sender, receiver, money))
                .isInstanceOf(MoneyNotFoundException.class);

        verify(transactionService).withdraw(money, false);
        verify(transactionService, never()).deposit(any(), anyBoolean());
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void testDeposit() {
    }

    @Test
    void testWithdraw() {
    }

    @Test
    void testGetLastTransactions() {
    }
}