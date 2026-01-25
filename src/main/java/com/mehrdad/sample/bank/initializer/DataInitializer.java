package com.mehrdad.sample.bank.initializer;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import com.mehrdad.sample.bank.core.util.AccountNumberGenerator;
import com.mehrdad.sample.bank.core.util.CustomerBusinessIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 5/26/2025
 * Time: 11:53 PM
 */
@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CustomerBusinessIdGenerator customerBusinessIdGenerator;

    @Override
    public void run(String... args) {
        String bankName = "BANK";
        String phoneNumber = "0011111111111";

        CustomerEntity bank = customerRepository.findByName(bankName)
                .orElseGet(() -> {
                    Integer businessId = customerBusinessIdGenerator.getNextBusinessId();
                    return customerRepository.save(createBank(bankName, businessId, phoneNumber));
                });

        String bankAccountNumber = AccountNumberGenerator.generate(bank.getBusinessId());

        if (!accountRepository.existsByNumber(bankAccountNumber)) {
            AccountEntity account = createBankAccount(bankAccountNumber, bank);
            bank.addAccount(account);
            account.setCustomer(bank);
            customerRepository.save(bank);
        }
    }

    private CustomerEntity createBank(String name, Integer businessId, String phonenumber) {
        CustomerEntity client;
        client = new CustomerEntity();
        client.setName(name);
        client.setBusinessId(businessId);
        client.setPhoneNumber(phonenumber);
        client.setStatus(Status.ACTIVE);
        client.setAccounts(new ArrayList<>());
        return client;
    }

    private AccountEntity createBankAccount(String bankAccountNumber, CustomerEntity bank) {
        AccountEntity account = new AccountEntity();
        account.setNumber(bankAccountNumber);
        account.setStatus(Status.ACTIVE);
        account.setCustomer(bank);
        return account;
    }
}
