package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountCreateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.core.entity.Currency;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.entity.TransactionType;
import com.mehrdad.sample.bank.core.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.core.exception.transaction.InvalidAmountException;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeAll;
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

    CustomerDto senderCustomer;
    AccountDto senderAccount;

    CustomerDto receiverCustomer;
    AccountDto receiverAccount;

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

    @BeforeEach
    void loadCustomers() {
        senderCustomer = customerService.createCustomer(new CustomerCreateDto("Alice", "1111111111", Status.ACTIVE));
        senderAccount = customerService.createAccount(senderCustomer.getId(), new AccountCreateDto());

        receiverCustomer =
                customerService.createCustomer(new CustomerCreateDto("Bob", "2222222222", Status.ACTIVE));
        receiverAccount =
                customerService.createAccount(receiverCustomer.getId(), new AccountCreateDto());
    }

    @Test
    void shouldTransferMoneyBetweenTwoAccounts() {
        // GIVEN
        // deposit money to sender so it has positive balance to create the transfer
        // create deposit transaction request
        transfer(bankCadAccount, senderAccount, new BigDecimal("100"), TransactionType.DEPOSIT);

        // WHEN
        transfer(senderAccount, receiverAccount, new BigDecimal("10"), TransactionType.TRANSFER);


        AccountDto updatedSender =
                accountService.getAccountById(senderAccount.getId());

        AccountDto updatedReceiver =
                accountService.getAccountById(receiverAccount.getId());

        // THEN
        assertEquals(new BigDecimal("90.0000"), updatedSender.getBalance());
        assertEquals(new BigDecimal("10.0000"), updatedReceiver.getBalance());

        assertEquals(2, transactionRepository.count()); // deposit + transfer
    }

    @Test
    void shouldDepositMoneyToAccount() {
        transfer(bankCadAccount, receiverAccount, new BigDecimal("100"), TransactionType.DEPOSIT);
    }

    @Test
    void shouldWithdrawMoneyFromAccount() {
        transfer(bankCadAccount, senderAccount, new BigDecimal("100.0000"), TransactionType.DEPOSIT);

        AccountDto senderAfterDeposit = accountService.getAccountById(senderAccount.getId());
        AccountDto bankAfterDeposit = accountService.getAccountById(bankCadAccount.getId());

        BigDecimal senderBalance = senderAfterDeposit.getBalance();
        BigDecimal bankBalance = bankAfterDeposit.getBalance();
        BigDecimal withdrawAmount = new BigDecimal("10.0000");

        //WHEN
        transfer(senderAccount, bankCadAccount, withdrawAmount, TransactionType.WITHDRAW);
        //THEN
        AccountDto updatedSender = accountService.getAccountById(senderAccount.getId());
        AccountDto updatedBank = accountService.getAccountById(bankCadAccount.getId());

        assertEquals(senderBalance.subtract(withdrawAmount), updatedSender.getBalance());
        assertEquals(bankBalance.add(withdrawAmount), updatedBank.getBalance());
    }

   

    private void transfer(AccountDto senderAccount, AccountDto receiverAccount, BigDecimal number, TransactionType deposit) {
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                number,
                Currency.CAD,
                deposit);

        transactionService.createTransaction(transactionRequest);
    }

}