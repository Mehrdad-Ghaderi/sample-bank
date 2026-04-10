package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.CreateDepositRequest;
import com.mehrdad.sample.bank.api.dto.CreateTransferRequest;
import com.mehrdad.sample.bank.api.dto.CreateWithdrawalRequest;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.AccountRole;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.entity.TransactionEntity;
import com.mehrdad.sample.bank.domain.entity.TransactionType;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.domain.exception.transaction.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidAmountException;
import com.mehrdad.sample.bank.domain.mapper.TransactionMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import com.mehrdad.sample.bank.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
    public TransactionDto transfer(CreateTransferRequest request) {
        validateAmount(request.getAmount());
        return createTransaction(
                request.getSenderAccountId(),
                request.getReceiverAccountId(),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.TRANSFER
        );
    }

    @Transactional
    public TransactionDto deposit(CreateDepositRequest request) {
        validateAmount(request.getAmount());
        AccountEntity bankTreasury = loadBankTreasuryAccountForUpdate(request.getCurrency());
        return createTransaction(
                bankTreasury.getId(),
                request.getReceiverAccountId(),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.DEPOSIT
        );
    }

    @Transactional
    public TransactionDto withdraw(CreateWithdrawalRequest request) {
        validateAmount(request.getAmount());
        AccountEntity bankTreasury = loadBankTreasuryAccountForUpdate(request.getCurrency());
        return createTransaction(
                request.getSenderAccountId(),
                bankTreasury.getId(),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.WITHDRAW
        );
    }

    private TransactionDto createTransaction(
            UUID senderId,
            UUID receiverId,
            BigDecimal amount,
            Currency currency,
            TransactionType type
    ) {
        LockedAccounts lockedAccounts = loadAccountsForUpdate(senderId, receiverId);
        validateTransaction(currency, type, lockedAccounts.sender(), lockedAccounts.receiver());

        lockedAccounts.sender().decreaseBalance(amount);
        lockedAccounts.receiver().increaseBalance(amount);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setSender(lockedAccounts.sender());
        transaction.setReceiver(lockedAccounts.receiver());
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setType(type);
        transaction.setTransactionTime(Instant.now());

        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    private LockedAccounts loadAccountsForUpdate(UUID senderId, UUID receiverId) {
        AccountEntity first;
        AccountEntity second;

        // Deterministic lock ordering turns contention into waiting instead of circular deadlock.
        if (senderId.compareTo(receiverId) < 0) {
            first = loadAccountByIdForUpdate(senderId);
            second = loadAccountByIdForUpdate(receiverId);
        } else {
            first = loadAccountByIdForUpdate(receiverId);
            second = loadAccountByIdForUpdate(senderId);
        }

        AccountEntity sender = senderId.equals(first.getId()) ? first : second;
        AccountEntity receiver = receiverId.equals(first.getId()) ? first : second;
        return new LockedAccounts(sender, receiver);
    }

    private AccountEntity loadAccountByIdForUpdate(UUID accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private AccountEntity loadBankTreasuryAccountForUpdate(Currency currency) {
        return accountRepository.findByAccountRoleAndCurrencyForUpdate(AccountRole.BANK_TREASURY, currency)
                .orElseThrow(() -> new IllegalStateException(
                        "Bank treasury account for currency " + currency + " was not found."
                ));
    }

    private void validateTransaction(
            Currency currency,
            TransactionType type,
            AccountEntity sender,
            AccountEntity receiver
    ) {
        validateStatus(sender);
        validateStatus(receiver);
        validateCurrency(currency, sender, receiver);
        validateTransactionType(type, sender, receiver);
    }

    private void validateStatus(AccountEntity account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountNotActiveException(account);
        }
    }

    private static void validateCurrency(Currency currency, AccountEntity sender, AccountEntity receiver) {
        if (!sender.getCurrency().equals(currency) || !receiver.getCurrency().equals(currency)) {
            throw new CurrencyMismatchException();
        }
    }

    private void validateTransactionType(TransactionType type, AccountEntity sender, AccountEntity receiver) {
        switch (type) {
            case TRANSFER -> {
                if (sender.getId().equals(receiver.getId())) {
                    throw new IllegalTransactionTypeException("Sender and receiver must differ");
                }
            }
            case DEPOSIT -> validateSystemAccount(sender, "Deposit sender must be the bank treasury account");
            case WITHDRAW -> validateSystemAccount(receiver, "Withdrawal receiver must be the bank treasury account");
        }
    }

    private void validateSystemAccount(AccountEntity account, String message) {
        if (account.getAccountRole() != AccountRole.BANK_TREASURY) {
            throw new IllegalTransactionTypeException(message);
        }
    }

    private record LockedAccounts(AccountEntity sender, AccountEntity receiver) {
    }
}
