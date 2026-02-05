package com.mehrdad.sample.bank.initializer;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.Currency;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import com.mehrdad.sample.bank.core.util.AccountNumberGenerator;
import com.mehrdad.sample.bank.core.util.CustomerBusinessIdGenerator;
import com.mehrdad.sample.bank.core.util.PhoneNumberNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 5/26/2025
 * Time: 11:53 PM
 */
@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

    private static final String BANK_NAME = "BANK";
    private static final String BANK_PHONE = "1234567890";
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000000000");
    private final CustomerRepository customerRepository;
    private final CustomerBusinessIdGenerator customerBusinessIdGenerator;

    @Override
    public void run(String... args) {

        CustomerEntity bank = customerRepository.findByName(BANK_NAME)
                .orElseGet(this::createBankCustomer);

        ensureBankAccounts(bank);
    }

    private CustomerEntity createBankCustomer() {
        String normalizedPhoneNumber = PhoneNumberNormalizer.normalizePhoneNumber(BANK_PHONE);
        CustomerEntity bank = new CustomerEntity();
        bank.setName(BANK_NAME);
        bank.setPhoneNumber(normalizedPhoneNumber);
        bank.setStatus(Status.ACTIVE);
        bank.setAccounts(new ArrayList<>());
        bank.setBusinessId(generateCustomerBusinessId());
        return customerRepository.saveAndFlush(bank);
    }

    private Integer generateCustomerBusinessId() {
        return customerBusinessIdGenerator.getNextBusinessId();
    }

    private void ensureBankAccounts(CustomerEntity bank) {
        // Get currencies of existing bank accounts
        Set<Currency> existingCurrencies = bank.getAccounts()
                .stream()
                .map(AccountEntity::getCurrency)
                .collect(Collectors.toSet());

        for (Currency currency : Currency.values()) {
            if (!existingCurrencies.contains(currency)) {
                // Bank doesn't have an account for this currency — create it
                String accountNumber = AccountNumberGenerator.generate(bank);
                AccountEntity account = new AccountEntity();
                account.setNumber(accountNumber);
                account.setCurrency(currency);
                account.setBalance(INITIAL_BALANCE);
                account.setStatus(Status.ACTIVE);
                account.setCustomer(bank);

                bank.addAccount(account);
            }
        }

        customerRepository.save(bank); // Persist new accounts
    }
}
