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
        Arrays.fill(chars, '*');
        return Arrays.toString(chars);
    }

    @Override
    public void setNumber(String number) {
        this.number = number;
    }
}
