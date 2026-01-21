package com.mehrdad.sample.bank.initializer;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 5/26/2025
 * Time: 11:53 PM
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Override
    public void run(String... args) {
        String defaultClientId = "111";
        String defaultAccountId = "111.1";

        // Step 1: Ensure the main bank(client and account exists
        ClientEntity bank;

        if (!clientRepository.existsById(defaultClientId)) {
            bank = createBank(defaultClientId);
        } else {
            bank = clientRepository.findById(defaultClientId).get();
        }
        clientRepository.save(bank);
        if (!accountRepository.existsById(defaultAccountId)) {
            AccountEntity account = createBankAccount(defaultAccountId, bank);

            bank.getAccounts().add(account);
            accountRepository.save(account);
            clientRepository.save(bank);
        }
    }

    private static AccountEntity createBankAccount(String defaultAccountId, ClientEntity bank) {
        AccountEntity account = new AccountEntity();
        account.setNumber(defaultAccountId);
        account.setActive(true);
        account.setClient(bank);
        return account;
    }

    private static ClientEntity createBank(String defaultClientId) {
        ClientEntity client;
        client = new ClientEntity();
        client.setId(defaultClientId);
        client.setName("BANK");
        client.setPhoneNumber("001111111111");
        client.setStatus(Status.ACTIVE);
        client.setAccounts(new ArrayList<>());
        return client;
    }
}
