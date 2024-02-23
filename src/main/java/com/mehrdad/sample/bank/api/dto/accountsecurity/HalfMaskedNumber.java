package com.mehrdad.sample.bank.api.dto.accountsecurity;

public class HalfMaskedNumber implements AccountNumber {
    private String number;

    public HalfMaskedNumber(String number) {
        this.number = number;
    }

    @Override
    public String getNumber() {
        String[] halves = number.split("\\."); //splits by dot
        halves[1] = ".*"; //masks the second part
        return halves[0] + halves[1];
    }

    @Override
    public void setNumber(String number) {
        this.number = number;
    }
}
