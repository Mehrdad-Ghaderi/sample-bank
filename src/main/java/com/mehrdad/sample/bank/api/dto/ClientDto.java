package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.api.dto.iterator.ListIterator;
import com.mehrdad.sample.bank.api.dto.iterator.Iterator;
import com.mehrdad.sample.bank.api.dto.textservice.Event;
import com.mehrdad.sample.bank.api.dto.textservice.Listener;
import com.mehrdad.sample.bank.api.dto.visitor.Visitable;
import com.mehrdad.sample.bank.api.dto.visitor.Visitor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ClientDto implements Listener {

    private String id;
    private String name;
    private String phoneNumber;
    private List<AccountDto> accounts;
    private Boolean active;

    public ClientDto(String id, String name, String phoneNumber, Boolean active) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.active = active;
    }

    public ClientDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.active = builder.active;
        this.accounts = builder.accounts;
    }

    public ClientDto() {
    }

    @Size(max = 10)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotBlank
    @Size(max = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank
    @Size(max = 15)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NotNull
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean status) {
        this.active = status;
    }

    public List<AccountDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return String.format("Client{id='%s', name='%s', phoneNumber='%s', active=%s, \naccounts=%s}",
                id, name, phoneNumber, active, accounts);
    }

    public String concatNameAndId() {
        return "{" + this.getName() + ", ID: " + this.getId() + "}";
    }

    @Override
    public void onEvent(Event event) {
        System.out.println("Dear " + this.getName() + ": \n" + event.getMessage());
    }

    public Iterator<AccountDto> createListIterator(List<AccountDto> accounts) {
        return new ListIterator(accounts);
    }

    public static class Builder {
        private String id;
        private String name;
        private String phoneNumber;
        private List<AccountDto> accounts;
        private Boolean active;

        public Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder accounts(List<AccountDto> accountDtos) {
            this.accounts = accountDtos;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public ClientDto build() {
            return new ClientDto(this);
        }
    }
}