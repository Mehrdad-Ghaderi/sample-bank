package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.core.entity.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateTransactionRequest {
    @NotNull
    UUID senderAccountId;

    @NotNull
    UUID receiverAccountId;

    @NotNull @Positive
    BigDecimal amount;

    @NotNull
    Currency currency;
}
