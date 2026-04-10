package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.domain.entity.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferRequest {
    @NotNull
    private UUID senderAccountId;

    @NotNull
    private UUID receiverAccountId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Currency currency;
}
