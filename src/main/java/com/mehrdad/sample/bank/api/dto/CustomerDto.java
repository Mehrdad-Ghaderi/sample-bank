package com.mehrdad.sample.bank.api.dto;


import com.mehrdad.sample.bank.core.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private UUID id;

    @NotBlank
    @Size(max = 45)
    private String name;

    @NotBlank
    private Integer businessId;

    @NotBlank
    @Size(max = 13)
    private String phoneNumber;

    private List<AccountDto> accounts;

    @NotNull
    private Status status;

    private Instant createdAt;
    private Instant updatedAt;

    public CustomerDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.accounts = builder.accounts;
        this.status = builder.status;
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String phoneNumber;
        private List<AccountDto> accounts;
        private Status status;

        public Builder() {
        }

        public Builder id(UUID id) {
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

        public CustomerDto build() {
            return new CustomerDto(this);
        }
    }
}