package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountCreateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.entity.TransactionType;
import com.mehrdad.sample.bank.domain.exception.transaction.CurrencyMismatchException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidAmountException;
import com.mehrdad.sample.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TransactionServiceIT {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    CustomerDto bankCustomer;
    AccountDto bankCadAccount;

    CustomerDto senderCustomer;
    AccountDto senderAccount;

    CustomerDto receiverCustomer;
    AccountDto receiverAccount;

    @BeforeEach
    void setup() {
        loadBank();
        loadCustomers();
    }

    void loadBank() {
        bankCustomer = customerService.getCustomerByName("BANK");
        bankCadAccount = accountService
                .getAccountsByCustomerId(bankCustomer.getId())
                .stream()
                .filter(a -> a.getCurrency() == Currency.CAD)
                .findFirst()
                .orElseThrow();
    }


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
        givenAccountHasBalance(senderAccount, new BigDecimal("100.0000"));

        // WHEN
        transfer(senderAccount, receiverAccount, new BigDecimal("10.0000"), TransactionType.TRANSFER);

        //THEN
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
    }

    @Test
    void shouldWithdrawMoneyFromAccount() {
        // GIVEN
        givenAccountHasBalance(senderAccount, new BigDecimal("100.0000"));

        // WHEN
        transfer(senderAccount, bankCadAccount, new BigDecimal("10.0000"), TransactionType.WITHDRAW);

        // THEN
        assertBalance(senderAccount, "90.0000");
    }

    @Test
    void shouldThrowInvalidAmountException() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                BigDecimal.ZERO,
                Currency.CAD,
                TransactionType.TRANSFER
        );

        assertThrows(InvalidAmountException.class, () -> transactionService.createTransaction(request));
    }

    @Test
    void shouldThrowCurrencyMismatchException() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                senderAccount.getId(),
                receiverAccount.getId(),
                BigDecimal.valueOf(10),
                Currency.USD,
                TransactionType.TRANSFER
        );

        assertThrows(CurrencyMismatchException.class, () -> transactionService.createTransaction(request));
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

    private void givenAccountHasBalance(AccountDto account, BigDecimal amount) {
        transfer(bankCadAccount, account, amount, TransactionType.DEPOSIT);
    }

    private void assertBalance(AccountDto account, String expected) {
        AccountDto refreshed = accountService.getAccountById(account.getId());
        assertEquals(new BigDecimal(expected), refreshed.getBalance());
    }
}