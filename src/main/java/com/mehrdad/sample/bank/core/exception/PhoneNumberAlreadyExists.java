package com.mehrdad.sample.bank.core.exception;

import jakarta.validation.constraints.Pattern;

public class PhoneNumberAlreadyExists extends RuntimeException {

    public PhoneNumberAlreadyExists(String phoneNumber) {
        super("Phone number : " + phoneNumber + " already exists.");
    }
}
