package com.mehrdad.sample.bank.api.dto;


import com.mehrdad.sample.bank.core.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
public class ClientDto {

    private String id;
    private String name;
    private String phoneNumber;
    private List<AccountDto> accounts;
    private Status status;

    public ClientDto(String id, String name, String phoneNumber, Status status) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public ClientDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.accounts = builder.accounts;
        this.status = status;
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
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<AccountDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s \n    accounts=%s",
                id, name, phoneNumber, status, accounts);
    }

    public String concatNameAndId() {
        return "{" + this.getName() + ", ID: " + this.getId() + "}";
    }



    public static class Builder {
        private String id;
        private String name;
        private String phoneNumber;
        private List<AccountDto> accounts;
        private Status status;

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

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public ClientDto build() {
            return new ClientDto(this);
        }
    }
}