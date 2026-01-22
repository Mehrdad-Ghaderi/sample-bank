package com.mehrdad.sample.bank.api.dto;


import com.mehrdad.sample.bank.core.entity.Status;
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
    private Status status;

    @NotNull
    private List<MoneyDto> moneys;

    public AccountDto(String number, Status status) {
        this.number = number;
        this.status = status;
    }


    @Override
    public String toString() {
        return String.format("number='%s', balance=%s, status=%s",
                number, printMoneys(), status);
    }

    private String printMoneys() {
        StringBuilder sb = new StringBuilder();
        for (MoneyDto money : moneys) {
            sb.append(money.toString()).append(" ");
        }
        return sb.toString();
    }

}