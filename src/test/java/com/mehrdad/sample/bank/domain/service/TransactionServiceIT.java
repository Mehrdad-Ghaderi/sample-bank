package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.CreateDepositRequest;
import com.mehrdad.sample.bank.api.dto.CreateTransferRequest;
import com.mehrdad.sample.bank.api.dto.CreateWithdrawalRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountCreateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.domain.entity.*;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotActiveException;
import com.mehrdad.sample.bank.domain.exception.account.AccountNotFoundException;
import com.mehrdad.sample.bank.domain.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.domain.exception.transaction.IllegalTransactionTypeException;
import com.mehrdad.sample.bank.domain.exception.transaction.InsufficientBalanceException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidAmountException;
import com.mehrdad.sample.bank.domain.mapper.AccountMapper;
import com.mehrdad.sample.bank.domain.repository.AccountRepository;
import com.mehrdad.sample.bank.domain.repository.CustomerRepository;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class TransactionServiceIT {

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

    AccountDto bankCadAccount;
    AccountDto senderAccount;
    AccountDto receiverAccount;
    @Autowired
    private AccountMapper accountMapper;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        loadBank();
        createSender();
        createReceiver();
    }

    void loadBank() {
        CustomerEntity bankEntity = new CustomerEntity();
        bankEntity.setName("BANK");
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
                .map(accountMapper::toAccountDto)
                .orElseThrow();
    }

    void createSender() {
        var senderCustomer = customerService.createCustomer(
                new CustomerCreateDto("Alice", "1111111111", Status.ACTIVE));
        senderAccount = customerService.createAccount(senderCustomer.getId(), new AccountCreateDto());
    }

    void createReceiver() {
        var receiverCustomer = customerService.createCustomer(
                new CustomerCreateDto("Bob", "2222222222", Status.ACTIVE));
        receiverAccount = customerService.createAccount(receiverCustomer.getId(), new AccountCreateDto());
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

        assertThrows(InvalidAmountException.class, () -> transactionService.transfer(request));
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

        assertThrows(CurrencyMismatchException.class, () -> transactionService.transfer(request));
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

        assertThrows(AccountNotFoundException.class, () -> transactionService.deposit(request));
    }

    @Test
    void shouldThrowWhenWithdrawalSourceAccountDoesNotExist() {
        CreateWithdrawalRequest request = new CreateWithdrawalRequest(
                UUID.randomUUID(),
                new BigDecimal("5.0000"),
                Currency.CAD
        );

        assertThrows(AccountNotFoundException.class, () -> transactionService.withdraw(request));
    }

    @Test
    void shouldThrowWhenTransactionUsesInactiveAccount() {
        givenAccountHasBalance(senderAccount, new BigDecimal("10.0000"));
        accountService.setAccountStatus(senderAccount.getId(), Status.FROZEN);

        assertThrows(AccountNotActiveException.class,
                () -> transfer(senderAccount, receiverAccount, new BigDecimal("5.0000"), TransactionType.TRANSFER));
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

            assertEquals(1, successes);
            assertEquals(1, insufficientBalanceFailures);
        }

        assertBalance(senderAccount, "40.0000");
        assertEquals(2, transactionRepository.count()); // initial deposit + one successful withdraw
    }

    private void transfer(
            AccountDto senderAccount,
            AccountDto receiverAccount,
            BigDecimal amount,
            TransactionType transactionType
    ) {
        switch (transactionType) {
            case TRANSFER -> transactionService.transfer(new CreateTransferRequest(
                    senderAccount.getId(),
                    receiverAccount.getId(),
                    amount,
                    Currency.CAD
            ));
            case DEPOSIT -> transactionService.deposit(new CreateDepositRequest(
                    receiverAccount.getId(),
                    amount,
                    Currency.CAD
            ));
            case WITHDRAW -> transactionService.withdraw(new CreateWithdrawalRequest(
                    senderAccount.getId(),
                    amount,
                    Currency.CAD
            ));
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
            transactionService.withdraw(request);
            return null;
        } catch (Throwable throwable) {
            return throwable;
        }
    }

    private void givenAccountHasBalance(AccountDto account, BigDecimal amount) {
        transfer(bankCadAccount, account, amount, TransactionType.DEPOSIT);
    }

    private void assertBalance(AccountDto account, String expected) {
        AccountDto refreshed = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal(expected), refreshed.getBalance());
    }
}
