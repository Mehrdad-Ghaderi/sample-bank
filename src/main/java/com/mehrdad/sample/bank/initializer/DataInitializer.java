package com.mehrdad.sample.bank.initializer;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 5/26/2025
 * Time: 11:53 PM
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        String bankName = "BANK";
        String bankAccountNumber = "1001-111-111111";

        // Step 1: Ensure the BANK customer exist
        CustomerEntity bank = customerRepository.findByName(bankName)
                .orElseGet(() -> createBank(bankName));

        // Step 2: Ensure Bank account exists
        if (!accountRepository.existsByNumber(bankAccountNumber)) {
            AccountEntity account = createBankAccount(bankAccountNumber, bank);
            accountRepository.save(account);
        }
    }

    private static CustomerEntity createBank(String name) {
        CustomerEntity client;
        client = new CustomerEntity();
        client.setName(name);
        client.setPhoneNumber("0013432021911");
        client.setStatus(Status.ACTIVE);
        client.setAccounts(new ArrayList<>());
        return client;
    }

    private static AccountEntity createBankAccount(String bankAccountNumber, CustomerEntity bank) {
        AccountEntity account = new AccountEntity();
        account.setNumber(bankAccountNumber);
        account.setStatus(Status.ACTIVE);
        account.setCustomer(bank);
        return account;
    }
}
