package com.mehrdad.sample.bank.api.dto.accountsecurity;

import java.util.Arrays;

public class FullyMaskedNumber implements AccountNumber {
    private String number;

    public FullyMaskedNumber(String number) {
        this.number = number;
    }

    @Override
    public String getNumber() {
        char[] chars = number.toCharArray();
        StringBuilder s = new StringBuilder();

        s.append("*".repeat(chars.length));
        return s.toString();
    }

    @Override
    public void setNumber(String number) {
        this.number = number;
    }
}
