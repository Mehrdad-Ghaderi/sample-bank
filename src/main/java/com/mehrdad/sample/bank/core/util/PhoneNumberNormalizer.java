package com.mehrdad.sample.bank.core.util;

import com.mehrdad.sample.bank.core.exception.customer.InvalidPhoneNumberException;

public final class PhoneNumberNormalizer {

    private static final String COUNTRY_CODE = "+1";

    public static String normalizePhoneNumber(String rawPhoneNumber) {
        if (rawPhoneNumber == null || rawPhoneNumber.isBlank()) {
            throw new InvalidPhoneNumberException("Phone number is required");
        }

        // remove everything except digits
        String digits = rawPhoneNumber.replaceAll("\\D", "");


        // reject if it starts with 0
        if (digits.startsWith("0")) {
            throw new InvalidPhoneNumberException(
                    "Phone number must not start with leading zeros"
            );
        }

        // must be exactly 10 digits (US/Canada)
        if (digits.length() != 10) {
            throw new InvalidPhoneNumberException(
                    "Phone number must contain exactly 10 digits."
            );
        }

        return COUNTRY_CODE + digits;
    }
}
