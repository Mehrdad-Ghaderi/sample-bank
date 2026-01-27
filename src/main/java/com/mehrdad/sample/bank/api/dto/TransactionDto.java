package com.mehrdad.sample.bank.api.dto;


import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.core.entity.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    @NotNull
    private UUID id;

    @NotNull
    private AccountDto sender;

    @NotNull
    private AccountDto receiver;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private Instant transactionTime;


    public TransactionDto(Builder builder) {
        this.id = builder.id;
        this.sender = builder.sender;
        this.receiver = builder.receiver;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.transactionTime = builder.transactionTime;
    }

    public static class Builder{
        private UUID id;
        private AccountDto sender;
        private AccountDto receiver;
        private BigDecimal amount;
        private Currency currency;
        private Instant transactionTime;

        public Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder sender(AccountDto sender) {
            this.sender = sender;
            return this;
        }
        public Builder receiver(AccountDto receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder transactionTie(Instant transactionTime) {
            this.transactionTime = transactionTime;
            return this;
        }

        public TransactionDto build() {
            return new TransactionDto(this);
        }
    }
}
