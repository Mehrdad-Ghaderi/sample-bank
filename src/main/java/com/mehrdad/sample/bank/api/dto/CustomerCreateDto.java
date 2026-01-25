package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.core.entity.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateDto {
    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    private Status status;
}

