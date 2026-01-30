package com.mehrdad.sample.bank.api.dto.account;


import com.mehrdad.sample.bank.core.entity.Currency;
import com.mehrdad.sample.bank.core.entity.Status;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private UUID id;

    private String number;

    private Status status;

    private Currency currency;

    private BigDecimal balance;

    private Instant createdAt;

    private Instant updatedAt;

}