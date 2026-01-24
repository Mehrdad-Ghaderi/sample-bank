package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.core.entity.Currency;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyDto {

    @NotNull
    private UUID id;

    @NotNull
    private Currency currency;

    @NotNull
    private BigDecimal amount;

    public MoneyDto(Builder builder) {
        this.id = builder.id;
        this.currency = builder.currency;
        this.amount = builder.amount;
    }

    @Override
    public String toString() {
        return currency.toString() +  amount;
    }

    public static class Builder {
        private UUID id;
        private Currency currency;
        private BigDecimal amount;

        public Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public MoneyDto build() {
            return new MoneyDto(this);
        }
    }

}

