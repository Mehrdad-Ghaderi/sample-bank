package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.core.entity.Status;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDto {

    @Size(max = 70)
    private String name;

    @Pattern(
            regexp = "^\\+?[0-9\\s\\-()]{10,20}$",
            message = "Invalid phone number format"
    )
    private String phoneNumber;
    private Status Status;
}
