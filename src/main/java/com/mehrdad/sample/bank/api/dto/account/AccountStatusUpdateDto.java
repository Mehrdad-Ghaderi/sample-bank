package com.mehrdad.sample.bank.api.dto.account;

import com.mehrdad.sample.bank.domain.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatusUpdateDto {

    @NotNull
    private Status status;
}
