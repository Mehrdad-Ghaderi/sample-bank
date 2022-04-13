package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import org.springframework.stereotype.Component;

@Component
public class MoneyMapper {

    private final AccountMapper accountMapper;
    private final ClientMapper clientMapper;

    public MoneyMapper(AccountMapper accountMapper, ClientMapper clientMapper) {
        this.accountMapper = accountMapper;
        this.clientMapper = clientMapper;
    }

    public MoneyEntity toMoneyEntity(MoneyDto moneyDto) {
        MoneyEntity moneyEntity = new MoneyEntity();
        moneyEntity.setId(moneyDto.getId());
        moneyEntity.setCurrency(moneyDto.getCurrency());
        moneyEntity.setAmount(moneyDto.getAmount());

        ClientEntity clientEntity = clientMapper.toClientEntity(moneyDto.getAccount().getClient());
        AccountEntity accountEntity = accountMapper.toAccountEntity(moneyDto.getAccount(), clientEntity);

        moneyEntity.setAccount(accountEntity);
        return moneyEntity;
    }

    public MoneyDto toMonetDto(MoneyEntity moneyEntity) {
        MoneyDto moneyDto = new MoneyDto();
        moneyDto.setId(moneyEntity.getId());
        moneyDto.setCurrency(moneyEntity.getCurrency());
        moneyDto.setAmount(moneyEntity.getAmount());

        ClientDto clientDto = clientMapper.toClientDto(moneyEntity.getAccount().getClient());
        AccountDto accountDto = accountMapper.toAccountDto(moneyEntity.getAccount(), clientDto);
        return moneyDto;
    }
}
