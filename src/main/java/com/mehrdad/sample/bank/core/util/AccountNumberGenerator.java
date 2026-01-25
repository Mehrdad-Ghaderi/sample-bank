package com.mehrdad.sample.bank.core.util;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class AccountNumberGenerator {

    private static final String BANK_CODE = "101";

    private AccountNumberGenerator() {}

    public static String generate(Integer businessId) {
        String year = String.valueOf(Year.now().getValue());

        return String.format(
                "%s-%s-%06d",
                year,
                BANK_CODE,
                businessId
        );
    }
}
