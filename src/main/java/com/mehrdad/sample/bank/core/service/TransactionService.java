package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.*;
import com.mehrdad.sample.bank.core.exception.*;
import com.mehrdad.sample.bank.core.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.core.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.TransactionMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

/*
    private static final String BANK_ACCOUNT_NUMBER = "1001-111-111111";

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    */
/**
     * Transfers balance from one account to another.
     * <p>
     * Atomic, consistent, and fully domain-driven.
     *//*

    public void transfer(
            TransactionDto dto
    ) {

        AccountEntity sender = loadAccount(dto.getSender());
        AccountEntity receiver = loadAccount(dto.getReceiver());

        assertActive(sender);
        assertActive(receiver);
        assertPositive(dto.getAmount());

        Balance senderBalance = sender.getBalance(dto.getCurrency())
                .orElseThrow(() ->
                        new BalanceNotFoundException(sender, dto.getCurrency()));

        assertSufficientBalance(sender, senderBalance, dto.getAmount());

        Balance receiverBalance = receiver.getOrCreateBalance(dto.getCurrency());

        // ---- domain mutations ----
        senderBalance.decrease(dto.getAmount());
        receiverBalance.increase(dto.getAmount());

        TransactionEntity transaction = recordTransaction(
                sender,
                receiver,
                dto.getCurrency(),
                dto.getAmount()
        );
    }

    */
/**
     * Withdraws balance from an account.
     *//*

    public TransactionDto withdraw(AccountDto accountDto, BigDecimal amount, Currency currency) {

        AccountEntity account = loadAccount(accountDto);

        assertActive(account);
        assertPositive(amount);

        Balance balance = account.getBalance(currency)
                .orElseThrow(() ->
                        new BalanceNotFoundException(account, currency));

        assertSufficientBalance(account, balance, amount);

        balance.decrease(amount);

        TransactionEntity transaction = recordTransaction(
                account,
                null,
                currency,
                amount
        );

        return transactionMapper.toTransactionDto(transaction);
    }


    */
/**
     * Deposits balance into an account.
     *//*

    public TransactionDto deposit(AccountDto accountDto, BigDecimal amount, Currency currency) {

        AccountEntity account = loadAccount(accountDto);

        assertActive(account);
        assertPositive(amount);

        Balance balance = account.getOrCreateBalance(currency);
        balance.increase(amount);

        TransactionEntity transaction = recordTransaction(
                null,
                account,
                currency,
                amount
        );

        return transactionMapper.toTransactionDto(transaction);
    }

    private AccountEntity loadAccount(AccountDto dto) {

        return accountRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new AccountNotFoundException(dto.getNumber()));
    }

    private TransactionEntity recordTransaction(
            AccountEntity sender,
            AccountEntity receiver,
            Currency currency,
            BigDecimal amount
    ) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        Balance balance = new Balance(amount, currency);
        transaction.setBalance(balance);

        transactionRepository.save(transaction);
        return transaction;
    }

    private void assertActive(AccountEntity account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountNotActiveException(account);
        }
    }

    private void assertPositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    private void assertSufficientBalance(
            AccountEntity accountEntity,
            Balance balance,
            BigDecimal amount) {
        if (balance.getAmount().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }
*/
}
