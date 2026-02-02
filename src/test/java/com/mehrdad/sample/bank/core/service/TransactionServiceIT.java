package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountCreateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.core.entity.Currency;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.entity.TransactionType;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
class TransactionServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
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
    private TransactionRepository transactionRepository;

    /*
    GIVEN two customers with accounts
    WHEN a transfer is made
    THEN balances change correctly and transaction is stored
    */
    CustomerDto bankCustomer;
    AccountDto bankCadAccount;

    @BeforeEach
    void setupBank() {
        // initializing BANK
        bankCustomer = customerService.createCustomer(
                new CustomerCreateDto("BANK", "1234567890", Status.ACTIVE)
        );

        bankCadAccount = customerService.createAccount(
                bankCustomer.getId(),
                new AccountCreateDto(Currency.CAD)
        );
    }

    @Test
    void shouldTransferMoneyBetweenTwoAccounts() {
        // GIVEN

        // creating sender and their account
        CustomerDto senderCustomer =
                customerService.createCustomer(new CustomerCreateDto("Alice", "11111111111", Status.ACTIVE));
        AccountDto senderAccount =
                customerService.createAccount(senderCustomer.getId(), new AccountCreateDto());

        // creating receiver and their account
        CustomerDto receiverCustomer =
                customerService.createCustomer(new CustomerCreateDto("Bob", "22222222222", Status.ACTIVE));
        AccountDto receiverAccount =
                customerService.createAccount(receiverCustomer.getId(), new AccountCreateDto());

        // deposit money to sender
        // create deposit transaction request
        CreateTransactionRequest deposit = new CreateTransactionRequest(
                bankCadAccount.getId(),
                senderAccount.getId(),
                new BigDecimal("100.00"),
                Currency.CAD,
                TransactionType.DEPOSIT);
        // make the deposit(from BANK to future Sender so it will have a positive balance
        transactionService.createTransaction(deposit);

        // make the actual intended transfer
        CreateTransactionRequest transfer = new CreateTransactionRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                new BigDecimal("10.00"),
                Currency.CAD,
                TransactionType.TRANSFER
        );
        // WHEN
        transactionService.createTransaction(transfer);

        // THEN
        AccountDto updatedSender =
                accountService.getAccountById(senderAccount.getId());

        AccountDto updatedReceiver =
                accountService.getAccountById(receiverAccount.getId());

        assertEquals(new BigDecimal("90.00"), updatedSender.getBalance());
        assertEquals(new BigDecimal("10.00"), updatedReceiver.getBalance());

        assertEquals(3, transactionRepository.count()); // deposit + transfer
    }

}