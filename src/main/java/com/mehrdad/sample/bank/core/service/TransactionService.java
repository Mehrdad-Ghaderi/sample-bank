package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.*;
import com.mehrdad.sample.bank.core.exception.CurrencyMismatchException;
import com.mehrdad.sample.bank.core.exception.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.core.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.core.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.core.mapper.TransactionMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public Page<TransactionDto> getTransactions(Pageable pageable) {
        System.out.println("hi");
        return transactionRepository.findAll(pageable).map(transactionMapper::toTransactionDto);
    }

    @Transactional
    public TransactionDto createTransaction(CreateTransactionRequest request) {

        AccountEntity sender = loadAccountById(request.getSenderAccountId());

        AccountEntity receiver = loadAccountById(request.getReceiverAccountId());

        validateTransaction(request, sender, receiver);

        // avoid deadlock
        if (sender.getId().compareTo(receiver.getId()) < 0) {
            sender.decreaseBalance(request.getAmount());
            receiver.increaseBalance(request.getAmount());
        } else {
            receiver.increaseBalance(request.getAmount());
            sender.decreaseBalance(request.getAmount());
        }

        TransactionEntity tx = new TransactionEntity();
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setAmount(request.getAmount());
        tx.setCurrency(request.getCurrency());
        tx.setType(request.getType());
        tx.setTransactionTime(Instant.now());

        TransactionEntity savedTransaction = transactionRepository.save(tx);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    private AccountEntity loadAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void validateTransaction(
            CreateTransactionRequest request,
            AccountEntity sender,
            AccountEntity receiver) {
        validateStatus(sender);
        validateStatus(receiver);
        validateCurrency(request, sender, receiver);
        validateTransactionType(request, sender, receiver);
    }

    private void validateStatus(AccountEntity account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountNotActiveException(account);
        }
    }

    private static void validateCurrency(CreateTransactionRequest request, AccountEntity sender, AccountEntity receiver) {
        if (!sender.getCurrency().equals(request.getCurrency())
                || !receiver.getCurrency().equals(request.getCurrency())) {
            throw new CurrencyMismatchException();
        }
    }

    private void validateTransactionType(CreateTransactionRequest request, AccountEntity sender, AccountEntity receiver) {
        switch (request.getType()) {

            case TRANSFER -> {
                if (sender.getId().equals(receiver.getId())) {
                    throw new IllegalTransactionTypeException("Sender and receiver must differ");
                }
            }

            case DEPOSIT -> {
                if (!isBank(sender)) {
                    throw new IllegalTransactionTypeException("Deposit's sender must be the bank");
                }
            }

            case WITHDRAW -> {
                if (!isBank(receiver)) {
                    throw new IllegalTransactionTypeException("Withdrawal's receiver must be the bank");
                }
            }
        }
    }

    private boolean isBank(AccountEntity account) {
        return "BANK".equals(account.getCustomer().getName());
    }
}
