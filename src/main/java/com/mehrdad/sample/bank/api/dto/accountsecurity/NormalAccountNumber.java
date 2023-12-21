package com.mehrdad.sample.bank.api.dto.accountsecurity;

public class NormalAccountNumber implements AccountNumber {
    private String number;

    public NormalAccountNumber(String number) {
        this.number = number;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public void setNumber(String number) {
        this.number = number;
    }
}
