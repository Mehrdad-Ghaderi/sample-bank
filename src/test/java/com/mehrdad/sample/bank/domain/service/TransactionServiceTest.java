package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.transaction.CreateDepositRequest;
import com.mehrdad.sample.bank.api.dto.transaction.CreateTransferRequest;
import com.mehrdad.sample.bank.api.dto.transaction.CreateWithdrawalRequest;
import com.mehrdad.sample.bank.api.dto.transaction.TransactionResponse;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.AccountRole;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import com.mehrdad.sample.bank.domain.entity.TransactionEntity;
import com.mehrdad.sample.bank.domain.mapper.TransactionMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import com.mehrdad.sample.bank.domain.repository.IdempotencyRecordRepository;
import com.mehrdad.sample.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final String OWNER_USERNAME = "user";
    private static final String OTHER_USERNAME = "other-user";

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getTransactionsShouldSearchByTrimmedAccountNumber() {
        String accountNumber = "2026-101-000046-001";
        PageRequest pageable = PageRequest.of(0, 5);
        TransactionEntity transaction = new TransactionEntity();
        TransactionResponse transactionResponse = new TransactionResponse();

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(customerAccount(OWNER_USERNAME)));
        when(transactionRepository.searchTransactionsByOwner(OWNER_USERNAME, accountNumber, pageable))
                .thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.mapToTransactionResponse(transaction)).thenReturn(transactionResponse);

        var result = transactionService.getTransactions(OWNER_USERNAME, "  " + accountNumber + "  ", pageable);

        assertEquals(List.of(transactionResponse), result.getContent());
        verify(accountRepository).findByNumber(accountNumber);
        verify(transactionRepository).searchTransactionsByOwner(OWNER_USERNAME, accountNumber, pageable);
        verify(transactionMapper).mapToTransactionResponse(transaction);
    }

    @Test
    void getTransactionsShouldTreatBlankAccountNumberAsNoAccountNumberFilter() {
        PageRequest pageable = PageRequest.of(0, 5);

        when(transactionRepository.searchTransactionsByOwner(OWNER_USERNAME, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        var result = transactionService.getTransactions(OWNER_USERNAME, "   ", pageable);

        assertEquals(List.of(), result.getContent());
        verify(transactionRepository).searchTransactionsByOwner(OWNER_USERNAME, null, pageable);
        verifyNoInteractions(transactionMapper);
    }

    @Test
    void getTransactionsShouldRejectAccountOwnedByAnotherUser() {
        String accountNumber = "2026-101-000046-001";
        PageRequest pageable = PageRequest.of(0, 5);

        when(accountRepository.findByNumber(accountNumber)).thenReturn(Optional.of(customerAccount(OTHER_USERNAME)));

        assertThrows(AccessDeniedException.class,
                () -> transactionService.getTransactions(OWNER_USERNAME, accountNumber, pageable));

        verify(accountRepository).findByNumber(accountNumber);
        verifyNoInteractions(transactionRepository, transactionMapper);
    }

    @Test
    void transferShouldRejectSenderOwnedByAnotherUser() {
        CreateTransferRequest request = new CreateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("25.50"),
                Currency.CAD
        );

        when(accountRepository.existsById(request.getSenderAccountId())).thenReturn(true);
        when(accountRepository.existsByIdAndAccountRole(request.getSenderAccountId(), AccountRole.CUSTOMER)).thenReturn(true);
        when(accountRepository.existsByIdAndAccountRoleAndOwnerUsername(
                request.getSenderAccountId(),
                AccountRole.CUSTOMER,
                OWNER_USERNAME
        )).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> transactionService.transfer(request, "key-1", OWNER_USERNAME));

        verify(accountRepository).existsById(request.getSenderAccountId());
        verify(accountRepository).existsByIdAndAccountRole(request.getSenderAccountId(), AccountRole.CUSTOMER);
        verify(accountRepository).existsByIdAndAccountRoleAndOwnerUsername(
                request.getSenderAccountId(),
                AccountRole.CUSTOMER,
                OWNER_USERNAME
        );
        verifyNoInteractions(transactionRepository, idempotencyRecordRepository, transactionMapper);
    }

    @Test
    void depositShouldRejectReceiverOwnedByAnotherUser() {
        CreateDepositRequest request = new CreateDepositRequest(
                UUID.randomUUID(),
                new BigDecimal("25.50"),
                Currency.CAD
        );

        when(accountRepository.existsById(request.getReceiverAccountId())).thenReturn(true);
        when(accountRepository.existsByIdAndAccountRole(request.getReceiverAccountId(), AccountRole.CUSTOMER)).thenReturn(true);
        when(accountRepository.existsByIdAndAccountRoleAndOwnerUsername(
                request.getReceiverAccountId(),
                AccountRole.CUSTOMER,
                OWNER_USERNAME
        )).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> transactionService.deposit(request, "key-1", OWNER_USERNAME));

        verify(accountRepository).existsById(request.getReceiverAccountId());
        verify(accountRepository).existsByIdAndAccountRole(request.getReceiverAccountId(), AccountRole.CUSTOMER);
        verify(accountRepository).existsByIdAndAccountRoleAndOwnerUsername(
                request.getReceiverAccountId(),
                AccountRole.CUSTOMER,
                OWNER_USERNAME
        );
        verifyNoInteractions(transactionRepository, idempotencyRecordRepository, transactionMapper);
    }

    @Test
    void withdrawShouldRejectSenderOwnedByAnotherUser() {
        CreateWithdrawalRequest request = new CreateWithdrawalRequest(
                UUID.randomUUID(),
                new BigDecimal("25.50"),
                Currency.CAD
        );

        when(accountRepository.existsById(request.getSenderAccountId())).thenReturn(true);
        when(accountRepository.existsByIdAndAccountRole(request.getSenderAccountId(), AccountRole.CUSTOMER)).thenReturn(true);
        when(accountRepository.existsByIdAndAccountRoleAndOwnerUsername(
                request.getSenderAccountId(),
                AccountRole.CUSTOMER,
                OWNER_USERNAME
        )).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> transactionService.withdraw(request, "key-1", OWNER_USERNAME));

        verify(accountRepository).existsById(request.getSenderAccountId());
        verify(accountRepository).existsByIdAndAccountRole(request.getSenderAccountId(), AccountRole.CUSTOMER);
        verify(accountRepository).existsByIdAndAccountRoleAndOwnerUsername(
                request.getSenderAccountId(),
                AccountRole.CUSTOMER,
                OWNER_USERNAME
        );
        verifyNoInteractions(transactionRepository, idempotencyRecordRepository, transactionMapper);
    }

    @Test
    void transferShouldRejectTreasurySenderAsOwnedCustomerAccount() {
        CreateTransferRequest request = new CreateTransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("25.50"),
                Currency.CAD
        );

        when(accountRepository.existsById(request.getSenderAccountId())).thenReturn(true);
        when(accountRepository.existsByIdAndAccountRole(request.getSenderAccountId(), AccountRole.CUSTOMER)).thenReturn(false);

        assertThrows(com.mehrdad.sample.bank.domain.exception.transaction.IllegalTransactionTypeException.class,
                () -> transactionService.transfer(request, "key-1", OWNER_USERNAME));

        verify(accountRepository).existsById(request.getSenderAccountId());
        verify(accountRepository).existsByIdAndAccountRole(request.getSenderAccountId(), AccountRole.CUSTOMER);
        verifyNoInteractions(transactionRepository, idempotencyRecordRepository, transactionMapper);
    }

    private static AccountEntity customerAccount(String ownerUsername) {
        return account(ownerUsername, AccountRole.CUSTOMER);
    }

    private static AccountEntity account(String ownerUsername, AccountRole accountRole) {
        CustomerEntity customer = new CustomerEntity();
        customer.setOwnerUsername(ownerUsername);

        AccountEntity account = new AccountEntity();
        account.setCustomer(customer);
        account.setAccountRole(accountRole);
        return account;
    }
}
