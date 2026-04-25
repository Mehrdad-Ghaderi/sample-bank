package com.mehrdad.sample.bank.domain.exception.customer;

public class PhoneNumberAlreadyExistsException extends RuntimeException {

    public PhoneNumberAlreadyExistsException() {
        super("Phone number already exists.");
    }

    public PhoneNumberAlreadyExistsException(String phoneNumber) {
        super("Phone number " + phoneNumber + " already exists.");
    }
}
