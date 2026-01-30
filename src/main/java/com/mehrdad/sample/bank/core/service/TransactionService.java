package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.*;
import com.mehrdad.sample.bank.core.exception.CurrencyMismatchException;
import com.mehrdad.sample.bank.core.exception.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.core.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.core.mapper.TransactionMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TransactionDto createTransaction(CreateTransactionRequest transactionRequestDto) {
        AccountEntity senderAccount = accountRepository.findById(transactionRequestDto.getSenderAccountId())
                .orElseThrow(() -> new AccountNotFoundException(transactionRequestDto.getSenderAccountId()));

        AccountEntity receiverAccount = accountRepository.findById(transactionRequestDto.getReceiverAccountId())
                .orElseThrow(() -> new AccountNotFoundException(transactionRequestDto.getReceiverAccountId()));

        validateTransaction(transactionRequestDto, senderAccount, receiverAccount);

        senderAccount.decreaseBalance(transactionRequestDto.getAmount());
        receiverAccount.increaseBalance(transactionRequestDto.getAmount());

        TransactionEntity tx = new TransactionEntity();
        tx.setSender(senderAccount);
        tx.setReceiver(receiverAccount);
        tx.setAmount(transactionRequestDto.getAmount());
        tx.setCurrency(transactionRequestDto.getCurrency());
        tx.setType(transactionRequestDto.getType());

        TransactionEntity savedTransaction = transactionRepository.save(tx);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    private void validateTransaction(
            CreateTransactionRequest req,
            AccountEntity sender,
            AccountEntity receiver
    ) {
        if (!sender.getCurrency().equals(req.getCurrency())
                || !receiver.getCurrency().equals(req.getCurrency())) {
            throw new CurrencyMismatchException();
        }

        switch (req.getType()) {

            case TRANSFER -> {
                if (sender.getId().equals(receiver.getId())) {
                    throw new IllegalTransactionTypeException("Sender and receiver must differ");
                }
            }

            case DEPOSIT -> {
                if (!isBankAccount(sender)) {
                    throw new IllegalTransactionTypeException("Deposit's sender must be the bank");
                }
            }

            case WITHDRAW -> {
                if (!isBankAccount(receiver)) {
                    throw new IllegalTransactionTypeException("Withdrawal's receiver must be the bank");
                }
            }
        }
    }

    private boolean isBankAccount(AccountEntity account) {
        return "BANK".equals(account.getCustomer().getName());
    }
}
