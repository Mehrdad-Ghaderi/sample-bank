package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountCreateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.domain.entity.*;
import com.mehrdad.sample.bank.domain.exception.transaction.CurrencyMismatchException;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

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
                // Bank doesn't have an account for this currency — create it
                String accountNumber = AccountNumberGenerator.generate(bankEntity);
                AccountEntity account = new AccountEntity();
                account.setNumber(accountNumber);
                account.setCurrency(currency);
                account.setBalance(new BigDecimal("1000000000"));
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
