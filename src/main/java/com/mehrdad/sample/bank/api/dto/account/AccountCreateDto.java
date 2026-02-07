package com.mehrdad.sample.bank.api.dto.account;

import com.mehrdad.sample.bank.domain.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {

    private Currency currency;
}
