package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.transaction.CreateDepositRequest;
import com.mehrdad.sample.bank.api.dto.transaction.CreateTransferRequest;
import com.mehrdad.sample.bank.api.dto.transaction.CreateWithdrawalRequest;
import com.mehrdad.sample.bank.api.dto.account.CreateAccountRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.api.dto.customer.CreateCustomerRequest;
import com.mehrdad.sample.bank.domain.entity.*;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.domain.exception.transaction.IdempotencyKeyConflictException;
import com.mehrdad.sample.bank.domain.exception.transaction.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.domain.exception.transaction.InsufficientBalanceException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidAmountException;
import com.mehrdad.sample.bank.domain.mapper.AccountMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import com.mehrdad.sample.bank.domain.repository.CustomerRepository;
import com.mehrdad.sample.bank.domain.repository.IdempotencyRecordRepository;
import com.mehrdad.sample.bank.domain.repository.TransactionRepository;
import com.mehrdad.sample.bank.domain.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class TransactionServiceIT {

    private static final String OWNER_USERNAME = "user";
    private static final String BANK_OWNER_USERNAME = "system";
    private AtomicInteger idempotencyKeySequence;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("sample_bank")
            .withUsername("sample_bank")
            .withPassword("sample_bank");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    AccountResponse bankCadAccount;
    AccountResponse senderAccount;
    AccountResponse receiverAccount;
    @Autowired
    private AccountMapper accountMapper;

    @BeforeEach
    void setup() {
        idempotencyRecordRepository.deleteAll();
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        idempotencyKeySequence = new AtomicInteger();
        loadBank();
        createSender();
        createReceiver();
    }

    void loadBank() {
        CustomerEntity bankEntity = new CustomerEntity();
        bankEntity.setName("BANK");
        bankEntity.setOwnerUsername(BANK_OWNER_USERNAME);
        bankEntity.setPhoneNumber("+10000000000");
        bankEntity.setStatus(Status.ACTIVE);
        bankEntity.setAccounts(new ArrayList<>());
        bankEntity.setBusinessId(100000);
        customerRepository.saveAndFlush(bankEntity);

        Set<Currency> existingCurrencies = bankEntity.getAccounts()
                .stream()
                .map(AccountEntity::getCurrency)
                .collect(Collectors.toSet());

        for (Currency currency : Currency.values()) {
            if (!existingCurrencies.contains(currency)) {
                // Bank does not have an account for this currency yet, so create it.
                String accountNumber = AccountNumberGenerator.generate(bankEntity);
                AccountEntity account = new AccountEntity();
                account.setNumber(accountNumber);
                account.setCurrency(currency);
                account.setBalance(new BigDecimal("1000000000"));
                account.setAccountRole(AccountRole.BANK_TREASURY);
                account.setStatus(Status.ACTIVE);
                account.setCustomer(bankEntity);

                bankEntity.addAccount(account);
            }
        }

        customerRepository.saveAndFlush(bankEntity);
        String bankCadAccountNumber = bankEntity.getAccounts().stream()
                .filter(a -> a.getCurrency().equals(Currency.CAD))
                .findFirst()
                .map(AccountEntity::getNumber)
                .orElseThrow();
        bankCadAccount = accountRepository.findByNumber(bankCadAccountNumber)
                .map(accountMapper::mapToAccountResponse)
                .orElseThrow();
    }

    void createSender() {
        var senderCustomer = customerService.createCustomer(
                new CreateCustomerRequest("Alice", "1111111111"), OWNER_USERNAME);
        senderAccount = customerService.createAccount(senderCustomer.getId(), new CreateAccountRequest(Currency.CAD), OWNER_USERNAME);
    }

    void createReceiver() {
        var receiverCustomer = customerService.createCustomer(
                new CreateCustomerRequest("Bob", "2222222222"), OWNER_USERNAME);
        receiverAccount = customerService.createAccount(receiverCustomer.getId(), new CreateAccountRequest(Currency.CAD), OWNER_USERNAME);
    }

    @Test
    void shouldTransferMoneyBetweenTwoAccounts() {
        // GIVEN
        givenAccountHasBalance(senderAccount, new BigDecimal("100.0000"));
        // WHEN
        transfer(senderAccount, receiverAccount, new BigDecimal("10.0000"), TransactionType.TRANSFER);
        // THEN
        assertBalance(senderAccount, "90.0000");
        assertBalance(receiverAccount, "10.0000");
        assertEquals(2, transactionRepository.count()); // deposit + transfer
    }

    @Test
    void shouldDepositMoneyToAccount() {
        // WHEN
        givenAccountHasBalance(receiverAccount, new BigDecimal("100.0000"));

        // THEN
        assertBalance(receiverAccount, "100.0000");
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void shouldWithdrawMoneyFromAccount() {
        // GIVEN
        givenAccountHasBalance(senderAccount, new BigDecimal("100.0000"));

        // WHEN
        transfer(senderAccount, bankCadAccount, new BigDecimal("10.0000"), TransactionType.WITHDRAW);

        // THEN
        assertBalance(senderAccount, "90.0000");
        assertEquals(2, transactionRepository.count()); // deposit + withdraw
    }

    @Test
    void shouldThrowInvalidAmountException() {
        CreateTransferRequest request = new CreateTransferRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                BigDecimal.ZERO,
                Currency.CAD
        );

        assertThrows(InvalidAmountException.class, () -> transactionService.transfer(request, nextIdempotencyKey("invalid-amount"), OWNER_USERNAME));
        assertEquals(0, transactionRepository.count());
    }

    @Test
    void shouldThrowCurrencyMismatchException() {
        CreateTransferRequest request = new CreateTransferRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                BigDecimal.valueOf(10),
                Currency.USD
        );

        assertThrows(CurrencyMismatchException.class, () -> transactionService.transfer(request, nextIdempotencyKey("currency-mismatch"), OWNER_USERNAME));
        assertEquals(0, transactionRepository.count());
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenSenderBalanceIsTooLow() {
        givenAccountHasBalance(senderAccount, new BigDecimal("10.0000"));

        assertThrows(InsufficientBalanceException.class,
                () -> transfer(senderAccount, receiverAccount, new BigDecimal("50.0000"), TransactionType.TRANSFER));

        assertBalance(senderAccount, "10.0000");
        assertBalance(receiverAccount, "0.0000");
        assertEquals(1, transactionRepository.count()); // initial deposit only
    }

    @Test
    void shouldThrowWhenTransferUsesSameSenderAndReceiverAccount() {
        givenAccountHasBalance(senderAccount, new BigDecimal("10.0000"));

        assertThrows(IllegalTransactionTypeException.class,
                () -> transfer(senderAccount, senderAccount, new BigDecimal("5.0000"), TransactionType.TRANSFER));
    }

    @Test
    void shouldThrowWhenDepositTargetAccountDoesNotExist() {
        CreateDepositRequest request = new CreateDepositRequest(
                UUID.randomUUID(),
                new BigDecimal("5.0000"),
                Currency.CAD
        );

        assertThrows(AccountNotFoundException.class, () -> transactionService.deposit(request, nextIdempotencyKey("missing-deposit"), OWNER_USERNAME));
    }

    @Test
    void shouldThrowWhenWithdrawalSourceAccountDoesNotExist() {
        CreateWithdrawalRequest request = new CreateWithdrawalRequest(
                UUID.randomUUID(),
                new BigDecimal("5.0000"),
                Currency.CAD
        );

        assertThrows(AccountNotFoundException.class, () -> transactionService.withdraw(request, nextIdempotencyKey("missing-withdrawal"), OWNER_USERNAME));
    }

    @Test
    void shouldThrowWhenTransactionUsesInactiveAccount() {
        givenAccountHasBalance(senderAccount, new BigDecimal("10.0000"));
        accountService.setAccountStatus(senderAccount.getId(), Status.FROZEN);

        assertThrows(AccountNotActiveException.class,
                () -> transfer(senderAccount, receiverAccount, new BigDecimal("5.0000"), TransactionType.TRANSFER));
    }

    @Test
    void shouldReturnOriginalTransferWhenIdempotencyKeyIsRetried() {
        givenAccountHasBalance(senderAccount, new BigDecimal("100.0000"));
        CreateTransferRequest request = new CreateTransferRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                new BigDecimal("10.0000"),
                Currency.CAD
        );

        var first = transactionService.transfer(request, "transfer-retry-1", OWNER_USERNAME);
        var second = transactionService.transfer(request, "transfer-retry-1", OWNER_USERNAME);

        assertEquals(first.getId(), second.getId());
        assertBalance(senderAccount, "90.0000");
        assertBalance(receiverAccount, "10.0000");
        assertEquals(2, transactionRepository.count()); // initial deposit + one transfer
        assertEquals(2, idempotencyRecordRepository.count()); // initial deposit + retried transfer command
    }

    @Test
    void shouldRejectReusedIdempotencyKeyWithDifferentTransferRequest() {
        givenAccountHasBalance(senderAccount, new BigDecimal("100.0000"));
        CreateTransferRequest firstRequest = new CreateTransferRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                new BigDecimal("10.0000"),
                Currency.CAD
        );
        CreateTransferRequest changedRequest = new CreateTransferRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                new BigDecimal("15.0000"),
                Currency.CAD
        );

        transactionService.transfer(firstRequest, "transfer-retry-2", OWNER_USERNAME);

        assertThrows(
                IdempotencyKeyConflictException.class,
                () -> transactionService.transfer(changedRequest, "transfer-retry-2", OWNER_USERNAME)
        );
        assertBalance(senderAccount, "90.0000");
        assertBalance(receiverAccount, "10.0000");
        assertEquals(2, transactionRepository.count()); // initial deposit + first transfer only
    }

    @Test
    void shouldSerializeConcurrentWithdrawalsOnSameSenderAccount() throws Exception {
        givenAccountHasBalance(senderAccount, new BigDecimal("100.0000"));

        CreateWithdrawalRequest withdrawRequest = new CreateWithdrawalRequest(
                senderAccount.getId(),
                new BigDecimal("60.0000"),
                Currency.CAD
        );

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<Throwable> task = () -> executeWithdrawalConcurrently(withdrawRequest, ready, start);

            Future<Throwable> first = executor.submit(task);
            Future<Throwable> second = executor.submit(task);

            ready.await();
            start.countDown();

            List<Throwable> outcomes = new ArrayList<>();
            outcomes.add(first.get());
            outcomes.add(second.get());
            long successes = outcomes.stream().filter(result -> result == null).count();
            long insufficientBalanceFailures = outcomes.stream()
                    .filter(InsufficientBalanceException.class::isInstance)
                    .count();
            String outcomeTypes = outcomes.stream()
                    .map(result -> result == null ? "success" : result.getClass().getName() + ": " + result.getMessage())
                    .toList()
                    .toString();

            assertEquals(1, successes, outcomeTypes);
            assertEquals(1, insufficientBalanceFailures, outcomeTypes);
        }

        assertBalance(senderAccount, "40.0000");
        assertEquals(2, transactionRepository.count()); // initial deposit + one successful withdraw
    }

    private void transfer(
            AccountResponse senderAccount,
            AccountResponse receiverAccount,
            BigDecimal amount,
            TransactionType transactionType
    ) {
        switch (transactionType) {
            case TRANSFER -> transactionService.transfer(new CreateTransferRequest(
                    senderAccount.getId(),
                    receiverAccount.getId(),
                    amount,
                    Currency.CAD
            ), nextIdempotencyKey("transfer"), OWNER_USERNAME);
            case DEPOSIT -> transactionService.deposit(new CreateDepositRequest(
                    receiverAccount.getId(),
                    amount,
                    Currency.CAD
            ), nextIdempotencyKey("deposit"), OWNER_USERNAME);
            case WITHDRAW -> transactionService.withdraw(new CreateWithdrawalRequest(
                    senderAccount.getId(),
                    amount,
                    Currency.CAD
            ), nextIdempotencyKey("withdraw"), OWNER_USERNAME);
        }
    }

    private Throwable executeWithdrawalConcurrently(
            CreateWithdrawalRequest request,
            CountDownLatch ready,
            CountDownLatch start
    ) {
        ready.countDown();
        try {
            start.await();
            transactionService.withdraw(request, nextIdempotencyKey("concurrent-withdraw"), OWNER_USERNAME);
            return null;
        } catch (Throwable throwable) {
            return throwable;
        }
    }

    private void givenAccountHasBalance(AccountResponse account, BigDecimal amount) {
        transfer(bankCadAccount, account, amount, TransactionType.DEPOSIT);
    }

    private void assertBalance(AccountResponse account, String expected) {
        String ownerUsername = bankCadAccount.getId().equals(account.getId()) ? BANK_OWNER_USERNAME : OWNER_USERNAME;
        AccountResponse refreshed = accountService.getAccountById(account.getId(), ownerUsername);
        assertEquals(new BigDecimal(expected), refreshed.getBalance());
    }

    private String nextIdempotencyKey(String prefix) {
        return prefix + "-" + idempotencyKeySequence.incrementAndGet();
    }
}
