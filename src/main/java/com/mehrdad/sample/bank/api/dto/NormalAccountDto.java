package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.api.dto.accountsecurity.AccountNumber;
import com.mehrdad.sample.bank.api.dto.iterator.ListIterator;
import com.mehrdad.sample.bank.api.dto.textservice.Event;
import com.mehrdad.sample.bank.api.dto.visitor.Visitable;
import com.mehrdad.sample.bank.api.dto.visitor.Visitor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class NormalAccountDto implements AccountDto  {

    //private String number;
    private AccountNumber accountNumber;
    private ClientDto client;
    private Boolean active;
    private List<MoneyDto> moneys;

    public NormalAccountDto(AccountNumber accountNumber, ClientDto client, Boolean status) {
        this.accountNumber = accountNumber;
        this.client = client;
        this.active = status;
    }

    public NormalAccountDto(Builder builder) {
        this.accountNumber = builder.accountNumber;
        this.client = builder.client;
        this.active = builder.active;
        this.moneys = builder.moneys;
    }

    public NormalAccountDto() {
    }

    @NotBlank
    public String getNumber() {
        return accountNumber.getNumber();
    }

    public void setNumber(AccountNumber accountNumber) {
        this.accountNumber = accountNumber;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
    }

    @NotNull
    public List<MoneyDto> getMoneys() {
        return moneys;
    }

    public void setMoneys(List<MoneyDto> moneys) {
        this.moneys = moneys;
    }

    @NotNull
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("AccountDto{number='%s', client=%s, active=%s}",
                accountNumber.getNumber(), client != null ? client.concatNameAndId() : null, active);
    }

    @Override
    public void onEvent(Event event) {
        System.out.println(event);
    }

    public static class Builder {
        private AccountNumber accountNumber;
        private ClientDto client;
        private Boolean active;
        private List<MoneyDto> moneys;

        public Builder() {
        }

        public Builder accountNumber(AccountNumber accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder client(ClientDto client) {
            this.client = client;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder moneys(List<MoneyDto> moneys) {
            this.moneys = moneys;
            return this;
        }

        public NormalAccountDto builder() {
            return new NormalAccountDto(this);
        }
    }

    public ListIterator<MoneyDto> createIterator() {
        return new ListIterator<>(moneys);
    }

}