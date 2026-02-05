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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceIT {

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
    void loadBank() {
        bankCustomer = customerService.getCustomerByName("BANK");

        bankCadAccount = accountService
                .getAccountsByCustomerId(bankCustomer.getId())
                .stream()
                .filter(a -> a.getCurrency() == Currency.CAD)
                .findFirst()
                .orElseThrow();
    }

    @Test
    void shouldTransferMoneyBetweenTwoAccounts() {
        // GIVEN

        // creating sender and their account
        CustomerDto senderCustomer =
                customerService.createCustomer(new CustomerCreateDto("Alice", "1111111111", Status.ACTIVE));
        AccountDto senderAccount =
                customerService.createAccount(senderCustomer.getId(), new AccountCreateDto());

        // creating receiver and their account
        CustomerDto receiverCustomer =
                customerService.createCustomer(new CustomerCreateDto("Bob", "2222222222", Status.ACTIVE));
        AccountDto receiverAccount =
                customerService.createAccount(receiverCustomer.getId(), new AccountCreateDto());
        ;

        // deposit money to sender so it has positive balance
        // create deposit transaction request
        CreateTransactionRequest deposit = new CreateTransactionRequest(
                bankCadAccount.getId(),
                senderAccount.getId(),
                new BigDecimal("100"),
                Currency.CAD,
                TransactionType.DEPOSIT);

        // make the deposit(from BANK to future Sender so it will have a positive balance
        transactionService.createTransaction(deposit);

        // make the actual intended transfer
        CreateTransactionRequest transfer = new CreateTransactionRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                new BigDecimal("10"),
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

        assertEquals(new BigDecimal("90.0000"), updatedSender.getBalance());
        assertEquals(new BigDecimal("10.0000"), updatedReceiver.getBalance());

        assertEquals(2, transactionRepository.count()); // deposit + transfer
    }

}