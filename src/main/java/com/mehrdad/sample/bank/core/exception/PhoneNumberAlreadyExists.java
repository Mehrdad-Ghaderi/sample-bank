package com.mehrdad.sample.bank.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PhoneNumberAlreadyExists extends RuntimeException {

    public PhoneNumberAlreadyExists(String phoneNumber) {
        super("Phone number : " + phoneNumber + " already exists.");
    }
}
