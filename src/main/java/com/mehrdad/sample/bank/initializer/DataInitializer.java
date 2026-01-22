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
        UUID id = UUID.randomUUID();
        String defaultCustomerName = "BANK";
        String defaultAccountId = "111.1";

        // Step 1: Ensure the main bank(client and account exists
        CustomerEntity bank = customerRepository.findByName(defaultCustomerName)
                .orElseGet(() -> createBank(defaultCustomerName));

        customerRepository.save(bank);
        if (!accountRepository.existsById(defaultAccountId)) {
            AccountEntity account = createBankAccount(defaultAccountId, bank);

            bank.getAccounts().add(account);
            accountRepository.save(account);
            customerRepository.save(bank);
        }
    }

    private static AccountEntity createBankAccount(String defaultAccountId, CustomerEntity bank) {
        AccountEntity account = new AccountEntity();
        account.setNumber(defaultAccountId);
        account.setActive(true);
        account.setCustomer(bank);
        return account;
    }

    private static CustomerEntity createBank(String defaultCustomerId) {
        CustomerEntity client;
        client = new CustomerEntity();
        client.setName("BANK");
        client.setPhoneNumber("001111111111");
        client.setStatus(Status.ACTIVE);
        client.setAccounts(new ArrayList<>());
        return client;
    }
}
