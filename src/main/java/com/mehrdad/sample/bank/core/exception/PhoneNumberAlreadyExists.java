package com.mehrdad.sample.bank.core.exception;

public class PhoneNumberAlreadyExists extends RuntimeException {

    public PhoneNumberAlreadyExists(String phoneNumber) {
        super("Phone number : " + phoneNumber + " already exists.");
    }
}
