package com.mehrdad.sample.bank.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class AccountDto {

    private String number;
    private ClientDto client;
    private Boolean active;
    private List<MoneyDto> moneys;

    public AccountDto(String number, ClientDto client, Boolean status) {
        this.number = number;
        this.client = client;
        this.active = status;
    }

    public AccountDto() {
    }

    @NotBlank
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
                number, client != null ? client.concatNameAndId() : null, active);
    }

}