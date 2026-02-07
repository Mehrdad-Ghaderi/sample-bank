package com.mehrdad.sample.bank.api.dto.account;

import com.mehrdad.sample.bank.domain.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {

    private Status status;

}
