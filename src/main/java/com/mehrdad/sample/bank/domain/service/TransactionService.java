package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.CreateDepositRequest;
import com.mehrdad.sample.bank.api.dto.CreateTransferRequest;
import com.mehrdad.sample.bank.api.dto.CreateWithdrawalRequest;
import com.mehrdad.sample.bank.api.dto.TransactionResponse;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.AccountRole;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.IdempotencyRecordEntity;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.entity.TransactionEntity;
import com.mehrdad.sample.bank.domain.entity.TransactionType;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.domain.exception.transaction.IdempotencyKeyConflictException;
import com.mehrdad.sample.bank.domain.exception.transaction.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidAmountException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidIdempotencyKeyException;
import com.mehrdad.sample.bank.domain.mapper.TransactionMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import com.mehrdad.sample.bank.domain.repository.IdempotencyRecordRepository;
import com.mehrdad.sample.bank.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(String ownerUsername, String accountNumber, Pageable pageable) {
        String normalizedAccountNumber = normalizeOptionalAccountNumber(accountNumber);

        if (normalizedAccountNumber != null) {
            validateAccountOwnership(loadAccountByNumber(normalizedAccountNumber), ownerUsername, "Account does not belong to authenticated user");
        }

        return transactionRepository.searchTransactionsByOwner(ownerUsername, normalizedAccountNumber, pageable)
                .map(transactionMapper::mapToTransactionResponse);
    }

    @Transactional
    public TransactionResponse transfer(CreateTransferRequest request, String idempotencyKey, String ownerUsername) {
        validateAmount(request.getAmount());
        validateOwnedCustomerAccount(request.getSenderAccountId(), ownerUsername, "Sender account does not belong to authenticated user");
        return executeIdempotently(
                idempotencyKey,
                TransactionType.TRANSFER,
                transferRequestHash(request),
                () -> createTransaction(
                        request.getSenderAccountId(),
                        request.getReceiverAccountId(),
                        request.getAmount(),
                        request.getCurrency(),
                        TransactionType.TRANSFER
                )
        );
    }

    @Transactional
    public TransactionResponse deposit(CreateDepositRequest request, String idempotencyKey, String ownerUsername) {
        validateAmount(request.getAmount());
        validateOwnedCustomerAccount(request.getReceiverAccountId(), ownerUsername, "Receiver account does not belong to authenticated user");
        return executeIdempotently(
                idempotencyKey,
                TransactionType.DEPOSIT,
                depositRequestHash(request),
                () -> {
                    AccountEntity bankTreasury = loadBankTreasuryAccountForUpdate(request.getCurrency());
                    return createTransaction(
                            bankTreasury.getId(),
                            request.getReceiverAccountId(),
                            request.getAmount(),
                            request.getCurrency(),
                            TransactionType.DEPOSIT
                    );
                }
        );
    }

    @Transactional
    public TransactionResponse withdraw(CreateWithdrawalRequest request, String idempotencyKey, String ownerUsername) {
        validateAmount(request.getAmount());
        validateOwnedCustomerAccount(request.getSenderAccountId(), ownerUsername, "Sender account does not belong to authenticated user");
        return executeIdempotently(
                idempotencyKey,
                TransactionType.WITHDRAW,
                withdrawalRequestHash(request),
                () -> {
                    AccountEntity bankTreasury = loadBankTreasuryAccountForUpdate(request.getCurrency());
                    return createTransaction(
                            request.getSenderAccountId(),
                            bankTreasury.getId(),
                            request.getAmount(),
                            request.getCurrency(),
                            TransactionType.WITHDRAW
                    );
                }
        );
    }

    private TransactionEntity createTransaction(
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
        return savedTransaction;
    }

    private TransactionResponse executeIdempotently(
            String idempotencyKey,
            TransactionType commandType,
            String requestHash,
            Supplier<TransactionEntity> transactionSupplier
    ) {
        String normalizedKey = normalizeIdempotencyKey(idempotencyKey);

        return idempotencyRecordRepository.findByIdempotencyKeyAndCommandType(normalizedKey, commandType)
                .map(existingRecord -> resolveExistingIdempotencyRecord(existingRecord, requestHash, normalizedKey))
                .orElseGet(() -> createIdempotentTransaction(
                        normalizedKey,
                        commandType,
                        requestHash,
                        transactionSupplier
                ));
    }

    private TransactionResponse resolveExistingIdempotencyRecord(
            IdempotencyRecordEntity existingRecord,
            String requestHash,
            String idempotencyKey
    ) {
        if (!existingRecord.getRequestHash().equals(requestHash)) {
            throw new IdempotencyKeyConflictException(idempotencyKey);
        }
        return transactionMapper.mapToTransactionResponse(existingRecord.getTransaction());
    }

    private TransactionResponse createIdempotentTransaction(
            String idempotencyKey,
            TransactionType commandType,
            String requestHash,
            Supplier<TransactionEntity> transactionSupplier
    ) {
        IdempotencyRecordEntity record = new IdempotencyRecordEntity();
        record.setIdempotencyKey(idempotencyKey);
        record.setCommandType(commandType);
        record.setRequestHash(requestHash);
        idempotencyRecordRepository.saveAndFlush(record);

        TransactionEntity transaction = transactionSupplier.get();
        record.setTransaction(transaction);

        return transactionMapper.mapToTransactionResponse(transaction);
    }

    private String transferRequestHash(CreateTransferRequest request) {
        return sha256(String.join("|",
                request.getSenderAccountId().toString(),
                request.getReceiverAccountId().toString(),
                normalizeAmount(request.getAmount()),
                request.getCurrency().name()
        ));
    }

    private String depositRequestHash(CreateDepositRequest request) {
        return sha256(String.join("|",
                request.getReceiverAccountId().toString(),
                normalizeAmount(request.getAmount()),
                request.getCurrency().name()
        ));
    }

    private String withdrawalRequestHash(CreateWithdrawalRequest request) {
        return sha256(String.join("|",
                request.getSenderAccountId().toString(),
                normalizeAmount(request.getAmount()),
                request.getCurrency().name()
        ));
    }

    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidIdempotencyKeyException();
        }
        return idempotencyKey.trim();
    }

    private String normalizeAmount(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 hashing is not available", e);
        }
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

    private AccountEntity loadAccountByNumber(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
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

    private String normalizeOptionalAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return null;
        }
        return accountNumber.trim();
    }

    private void validateOwnedCustomerAccount(UUID accountId, String ownerUsername, String accessDeniedMessage) {
        if (ownerUsername == null || ownerUsername.isBlank()) {
            throw new AccessDeniedException("Authenticated username is required");
        }

        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        if (!accountRepository.existsByIdAndAccountRole(accountId, AccountRole.CUSTOMER)) {
            throw new IllegalTransactionTypeException("Only customer-owned accounts can be used for this operation");
        }

        if (!accountRepository.existsByIdAndAccountRoleAndOwnerUsername(accountId, AccountRole.CUSTOMER, ownerUsername)) {
            throw new AccessDeniedException(accessDeniedMessage);
        }
    }

    private void validateAccountOwnership(AccountEntity account, String ownerUsername, String accessDeniedMessage) {
        if (account.getAccountRole() != AccountRole.CUSTOMER) {
            throw new IllegalTransactionTypeException("Only customer-owned accounts can be used for this operation");
        }

        if (!ownerUsername.equals(account.getCustomer().getOwnerUsername())) {
            throw new AccessDeniedException(accessDeniedMessage);
        }
    }

    private record LockedAccounts(AccountEntity sender, AccountEntity receiver) {
    }
}
