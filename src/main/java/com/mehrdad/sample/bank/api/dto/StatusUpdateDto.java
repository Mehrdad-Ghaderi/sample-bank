package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.domain.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDto {
    private Status status;
}
