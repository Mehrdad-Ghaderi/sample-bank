package com.mehrdad.sample.bank.api.dto.customer;

import com.mehrdad.sample.bank.domain.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateDto {
    @NotBlank
    private String name;

    @NotBlank
    @Pattern(
            regexp = "^\\+?[0-9\\s\\-()]{10,20}$",
            message = "Invalid phone number format"
    )
    private String phoneNumber;

    private Status status;
}

