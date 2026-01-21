package com.mehrdad.sample.bank.api.dto;


import com.mehrdad.sample.bank.core.entity.Status;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    @Size(max = 10)
    private String id;
    @NotBlank
    @Size(max = 45)
    private String name;
    @NotBlank
    @Size(max = 15)
    private String phoneNumber;
    private List<AccountDto> accounts;
    @NotNull
    private Status status;

    public ClientDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.accounts = builder.accounts;
        this.status = builder.status;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s \n    accounts=%s",
                id, name, phoneNumber, status, accounts);
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