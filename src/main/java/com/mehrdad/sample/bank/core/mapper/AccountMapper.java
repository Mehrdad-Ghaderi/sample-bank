package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.NormalAccountDto;
import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.accountsecurity.NormalAccountNumber;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    private final MoneyMapper moneyMapper;

    public AccountMapper(MoneyMapper moneyMapper) {
        this.moneyMapper = moneyMapper;
    }

    public AccountDto toAccountDto(AccountEntity accountEntity, ClientDto clientDto) {
        if (accountEntity == null) {
            return null;
        }
        AccountDto accountDto = new NormalAccountDto();
        //accountDto.setNumber(accountEntity.getNumber());
        accountDto.setNumber(new NormalAccountNumber(accountEntity.getNumber()));
        accountDto.setClient(clientDto);
        accountDto.setActive(accountEntity.isActive());
        accountDto.setMoneys(moneyMapper.toMoneyDtoList(accountEntity.getMoneys(), accountDto));

        return accountDto;
    }

    public List<AccountDto> toAccountDtoList(List<AccountEntity> entityList, ClientDto clientDto) {
        if (entityList == null) {
            return null;
        }
        return entityList.parallelStream()
                .map(accountEntity -> toAccountDto(accountEntity, clientDto))
                .collect(Collectors.toList());
    }

    public AccountEntity toAccountEntity(AccountDto accountDto, ClientEntity clientEntity) {
        if (accountDto == null) {
            return null;
        }
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setNumber(accountDto.getNumber());
        accountEntity.setClient(clientEntity);
        accountEntity.setActive(accountDto.isActive());
        accountEntity.setMoneys(moneyMapper.toMoneyEntityList(accountDto.getMoneys(), accountEntity));

        return accountEntity;
    }

    public List<AccountEntity> toAccountEntityList(List<AccountDto> dtoList, ClientEntity clientEntity) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.parallelStream()
                .map(accountDto -> toAccountEntity(accountDto, clientEntity))
                .collect(Collectors.toList());
    }

}