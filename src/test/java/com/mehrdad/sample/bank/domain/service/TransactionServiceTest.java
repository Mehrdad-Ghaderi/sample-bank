package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.TransactionDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

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
        TransactionDto transactionDto = new TransactionDto();

        when(transactionRepository.searchTransactions(accountNumber, pageable))
                .thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.toTransactionDto(transaction)).thenReturn(transactionDto);

        var result = transactionService.getTransactions("  " + accountNumber + "  ", pageable);

        assertEquals(List.of(transactionDto), result.getContent());
        verify(transactionRepository).searchTransactions(accountNumber, pageable);
        verify(transactionMapper).toTransactionDto(transaction);
    }

    @Test
    void getTransactionsShouldTreatBlankAccountNumberAsNoAccountNumberFilter() {
        PageRequest pageable = PageRequest.of(0, 5);

        when(transactionRepository.searchTransactions(null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        var result = transactionService.getTransactions("   ", pageable);

        assertEquals(List.of(), result.getContent());
        verify(transactionRepository).searchTransactions(null, pageable);
        verifyNoInteractions(transactionMapper);
    }
}
