package com.mehrdad.sample.bank.core.util;

import java.util.concurrent.ThreadLocalRandom;

public class AccountNumberGenerator {

    private static final String BANK_CODE = "1001";

    private AccountNumberGenerator() {}

    public static String generate() {
        int branch = ThreadLocalRandom.current().nextInt(1000, 9999);
        int sequence = ThreadLocalRandom.current().nextInt(0, 999999);

        return String.format(
                "%s-%04d-%06d",
                BANK_CODE,
                branch,
                sequence
        );
    }
}
