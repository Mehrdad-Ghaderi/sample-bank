package com.mehrdad.sample.bank.api.dto.account;


import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.Status;
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
public class AccountDto {

    private UUID id;

    private String number;

    private Status status;

    private List<MoneyDto> moneys;

    private Instant createdAt;

    private Instant updatedAt;

}