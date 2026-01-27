package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.*;
import com.mehrdad.sample.bank.core.exception.*;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.MoneyMapper;
import com.mehrdad.sample.bank.core.mapper.TransactionMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.MoneyRepository;
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

    private static final String BANK_ACCOUNT_NUMBER = "1001-111-111111";

    private final AccountRepository accountRepository;
    private final MoneyRepository moneyRepository;
    private final TransactionRepository transactionRepository;
    private final MoneyMapper moneyMapper;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    /**
     * Transfers money from one account to another.
     * <p>
     * Atomic, consistent, and fully domain-driven.
     */
    public void transfer(
            TransactionDto dto
    ) {

        AccountEntity sender = loadAccount(dto.getSender());
        AccountEntity receiver = loadAccount(dto.getReceiver());

        assertActive(sender);
        assertActive(receiver);
        assertPositive(dto.getAmount());

        MoneyEntity senderMoney = sender.getMoney(dto.getCurrency())
                .orElseThrow(() ->
                        new MoneyNotFoundException(sender, dto.getCurrency()));

        assertSufficientBalance(sender, senderMoney, dto.getAmount());

        MoneyEntity receiverMoney = receiver.getOrCreateMoney(dto.getCurrency());

        // ---- domain mutations ----
        senderMoney.decrease(dto.getAmount());
        receiverMoney.increase(dto.getAmount());

        TransactionEntity transaction = recordTransaction(
                sender,
                receiver,
                dto.getCurrency(),
                dto.getAmount()
        );
    }

    /**
     * Withdraws money from an account.
     */
    public TransactionDto withdraw(AccountDto accountDto, BigDecimal amount, Currency currency) {

        AccountEntity account = loadAccount(accountDto);

        assertActive(account);
        assertPositive(amount);

        MoneyEntity money = account.getMoney(currency)
                .orElseThrow(() ->
                        new MoneyNotFoundException(account, currency));

        assertSufficientBalance(account, money, amount);

        money.decrease(amount);

        TransactionEntity transaction = recordTransaction(
                account,
                null,
                currency,
                amount
        );

        return transactionMapper.toTransactionDto(transaction);
    }


    /**
     * Deposits money into an account.
     */
    public TransactionDto deposit(AccountDto accountDto, BigDecimal amount, Currency currency) {

        AccountEntity account = loadAccount(accountDto);

        assertActive(account);
        assertPositive(amount);

        MoneyEntity money = account.getOrCreateMoney(currency);
        money.increase(amount);

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
        transaction.setCurrency(currency);
        transaction.setAmount(amount);

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
            MoneyEntity money,
            BigDecimal amount) {
        if (money.getAmount().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(accountEntity, money);
        }
    }
}
