package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.*;
import com.mehrdad.sample.bank.core.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.core.exception.transaction.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.core.exception.transaction.InvalidAmountException;
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
        return transactionRepository.findAll(pageable).map(transactionMapper::toTransactionDto);
    }

    @Transactional
    public TransactionDto createTransaction(CreateTransactionRequest request) {

        validateAmount(request);

        UUID senderId = request.getSenderAccountId();
        UUID receiverId = request.getReceiverAccountId();


        AccountEntity first;
        AccountEntity second;

        // deterministic lock order → deadlock-free
        if (senderId.compareTo(receiverId) < 0) {
            first = loadAccountByIdForUpdate(senderId);
            second = loadAccountByIdForUpdate(receiverId);
        } else {
            first = loadAccountByIdForUpdate(receiverId);
            second = loadAccountByIdForUpdate(senderId);
        }

        AccountEntity sender = senderId.equals(first.getId()) ? first : second;
        AccountEntity receiver = receiverId.equals(first.getId()) ? first : second;

        //stateful validation under lock
        validateTransaction(request, sender, receiver);

        sender.decreaseBalance(request.getAmount());
        receiver.increaseBalance(request.getAmount());

        TransactionEntity transaction = new TransactionEntity();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setType(request.getType());
        transaction.setTransactionTime(Instant.now());

        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    private void validateAmount(CreateTransactionRequest request) {
        if (request.getAmount().signum() <= 0) {
            throw new InvalidAmountException(request.getAmount());
        }
    }

    private AccountEntity loadAccountByIdForUpdate(UUID accountId) {
        return accountRepository.findByIdForUpdate(accountId)
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
                if (!isSystemAccount(sender)) {
                    throw new IllegalTransactionTypeException("Deposit's sender must be the bank");
                }
            }

            case WITHDRAW -> {
                if (!isSystemAccount(receiver)) {
                    throw new IllegalTransactionTypeException("Withdrawal's receiver must be the bank");
                }
            }
        }
    }

    private boolean isSystemAccount(AccountEntity account) {
        return "BANK".equals(account.getCustomer().getName());
    }
}
