package com.mehrdad.sample.bank.initializer;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 5/26/2025
 * Time: 11:53 PM
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public void run(String... args) throws Exception {
        String defaultClientId = "111";
        String defaultAccountId = "111.1";

        // Step 1: Ensure the client exists
        ClientEntity client;
        if (clientRepository.existsById(defaultClientId)) {
            client = clientRepository.findById(defaultClientId).get();
        } else {
            client = new ClientEntity();
            client.setId(defaultClientId);
            client.setPhoneNumber("001111111111");
        }

        // Step 2: Ensure the account exists and link to the client
        if (!accountRepository.existsById(defaultAccountId)) {
            AccountEntity account = new AccountEntity();
            account.setNumber(defaultAccountId);
            account.setClient(client); // Assign the actual entity
            account.setActive(true);
            accountRepository.save(account);
        }
        client = clientRepository.save(client);

    }
}
