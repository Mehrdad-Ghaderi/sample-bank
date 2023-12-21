package com.mehrdad.sample.bank.api.dto.accountdecorator;

import com.mehrdad.sample.bank.api.dto.textservice.Event;

public class VIPAccountDecoratorDto extends AccountDecoratorDto {
    public VIPAccountDecoratorDto(AccountDto accountDto) {
        super(accountDto);
    }

    public String getNumber() {
        return "VIP-" + super.getNumber();
    }

    @Override
    public void onEvent(Event event) {
        System.out.println(event);
    }
}
