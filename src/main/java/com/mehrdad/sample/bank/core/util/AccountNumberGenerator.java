package com.mehrdad.sample.bank.core.util;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;

import java.time.Year;
import java.util.List;

public class AccountNumberGenerator {

    private static final String BANK_CODE = "101";

    private AccountNumberGenerator() {
    }

    public static String generate(CustomerEntity customer) {
        //Threads for different customers run in parallel, but are locked on the same customer
        synchronized (customer) {
            return generateNextAccountNumber(customer);
        }
    }

    private static String generateNextAccountNumber(CustomerEntity customer) {
        String year = String.valueOf(Year.now().getValue());
        String bankCode = "101";
        int customerId = customer.getBusinessId();
        List<AccountEntity> existingAccounts = customer.getAccounts();

        // Find last sequence
        int nextSequence = 1;
        if (existingAccounts != null && !existingAccounts.isEmpty()) {
            nextSequence = existingAccounts.stream()
                    .map(AccountEntity::getNumber)
                    .map(number -> {
                        try {
                            return Integer.parseInt(number.split("-")[3]);
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
        }

        return String.format("%s-%s-%06d-%03d", year, bankCode, customerId, nextSequence);
    }
}
