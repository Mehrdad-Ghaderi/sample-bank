package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.core.entity.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionResponse {
    UUID id;
    UUID senderAccountId;
    UUID receiverAccountId;
    BigDecimal amount;
    Currency currency;
    Instant transactionTime;
}
