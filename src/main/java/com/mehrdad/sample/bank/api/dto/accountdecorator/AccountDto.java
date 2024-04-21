package com.mehrdad.sample.bank.api.dto.accountdecorator;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.accountsecurity.AccountNumber;
import com.mehrdad.sample.bank.api.dto.textservice.Listener;

import java.util.List;

public interface AccountDto extends Listener {

    String getNumber();

    void setNumber(AccountNumber accountNumber);

    ClientDto getClient();

    void setClient(ClientDto client);

    List<MoneyDto> getMoneys();

    void setMoneys(List<MoneyDto> moneys);

    Boolean isActive();

    void setActive(Boolean active);

    String toString();


}

