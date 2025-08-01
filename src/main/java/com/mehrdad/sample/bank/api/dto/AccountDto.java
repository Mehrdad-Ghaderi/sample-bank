package com.mehrdad.sample.bank.api.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @NotBlank
    private String number;

    @NotNull
    private Boolean active;

    @NotNull
    private List<MoneyDto> moneys;

    public AccountDto(String number, Boolean status) {
        this.number = number;
        this.active = status;
    }


    @Override
    public String toString() {
        return String.format("no='%s', balance=%s %s",
                number, printMoneys(), active? "active" : "deactivated");
    }

    private String printMoneys() {
        StringBuilder sb = new StringBuilder();
        for (MoneyDto money : moneys) {
            sb.append(money.toString()).append(" ");
        }
        return sb.toString();
    }

}