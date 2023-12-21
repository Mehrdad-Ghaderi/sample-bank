package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ClientDto {

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
}