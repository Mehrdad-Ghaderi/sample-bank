package com.mehrdad.sample.bank.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountDto {

    private String number;
    private ClientDto client;
    private Boolean active;

    public AccountDto(String number, ClientDto client, Boolean active) {
        this.number = number;
        this.client = client;
        this.active = active;
    }

    public AccountDto() {
    }

    @NotBlank
    @Size(max = 15)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
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
                number, client != null ? client.concatNameAndId() : null, active);
    }

}