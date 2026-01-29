package com.mehrdad.sample.bank.api.dto;


import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.core.entity.Currency;
import com.mehrdad.sample.bank.core.entity.TransactionType;
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
    private UUID id;
    private AccountDto sender;
    private AccountDto receiver;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType type;
    private Instant transactionTime;
}
