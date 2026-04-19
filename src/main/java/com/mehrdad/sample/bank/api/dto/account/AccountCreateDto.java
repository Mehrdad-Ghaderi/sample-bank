package com.mehrdad.sample.bank.api.dto.account;

import com.mehrdad.sample.bank.domain.entity.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {

    @NotNull
    private Currency currency;
}
