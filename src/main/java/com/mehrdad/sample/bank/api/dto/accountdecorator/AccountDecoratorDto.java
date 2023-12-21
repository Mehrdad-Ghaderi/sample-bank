package com.mehrdad.sample.bank.api.dto.accountdecorator;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.accountsecurity.AccountNumber;

import java.util.List;

public abstract class AccountDecoratorDto implements AccountDto {
    private final AccountDto decoratedAccountDto;

    public AccountDecoratorDto(AccountDto accountDto) {
        this.decoratedAccountDto = accountDto;
    }

    @Override
    public String getNumber() {
        return decoratedAccountDto.getNumber();
    }

    @Override
    public void setNumber(AccountNumber accountNumber) {
        decoratedAccountDto.setNumber(accountNumber);
    }

    @Override
    public ClientDto getClient() {
        return decoratedAccountDto.getClient();
    }

    @Override
    public void setClient(ClientDto client) {
        decoratedAccountDto.setClient(client);
    }

    @Override
    public List<MoneyDto> getMoneys() {
        return decoratedAccountDto.getMoneys();
    }

    @Override
    public void setMoneys(List<MoneyDto> moneys) {
        decoratedAccountDto.setMoneys(moneys);
    }

    @Override
    public Boolean isActive() {
        return decoratedAccountDto.isActive();
    }

    @Override
    public void setActive(Boolean active) {
        decoratedAccountDto.setActive(active);
    }

    @Override
    public String toString() {
        return decoratedAccountDto.toString();
    }
}

