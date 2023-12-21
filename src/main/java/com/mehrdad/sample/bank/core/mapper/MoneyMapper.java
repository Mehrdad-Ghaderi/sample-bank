package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MoneyMapper {

    public MoneyMapper() {
    }

    public MoneyEntity toMoneyEntity(MoneyDto moneyDto, AccountEntity accountEntity) {
        if (moneyDto == null) {
            return null;
        }

        MoneyEntity moneyEntity = new MoneyEntity();
        moneyEntity.setId(moneyDto.getId());
        moneyEntity.setCurrency(moneyDto.getCurrency());
        moneyEntity.setAmount(moneyDto.getAmount());
        moneyEntity.setAccount(accountEntity);

        return moneyEntity;
    }

    public MoneyDto toMoneyDto(MoneyEntity moneyEntity, AccountDto accountDto) {
        if (moneyEntity == null) {
            return null;
        }

        MoneyDto moneyDto = new MoneyDto();
        moneyDto.setId(moneyEntity.getId());
        moneyDto.setCurrency(moneyEntity.getCurrency());
        moneyDto.setAmount(moneyEntity.getAmount());
        moneyDto.setAccount(accountDto);

        return moneyDto;
    }

    public List<MoneyDto> toMoneyDtoList(List<MoneyEntity> moneyEntities, AccountDto accountDto) {
        if (moneyEntities == null) {
            return null;
        }
        return moneyEntities.parallelStream()
                .map(moneyEnt -> toMoneyDto(moneyEnt, accountDto))
                .collect(Collectors.toList());
    }

    public List<MoneyEntity> toMoneyEntityList(List<MoneyDto> moneyDtos, AccountEntity accountEntity) {
        if (moneyDtos == null) {
            return null;
        }
        return moneyDtos.parallelStream()
                .map(moneyDto -> toMoneyEntity(moneyDto, accountEntity))
                .collect(Collectors.toList());
    }
}
