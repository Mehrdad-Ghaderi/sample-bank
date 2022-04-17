package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    public AccountDto toAccountDto(AccountEntity accountEntity, ClientDto clientDto) {
        if (accountEntity == null) {
            return null;
        }
        com.mehrdad.sample.bank.api.dto.AccountDto accountDto = new com.mehrdad.sample.bank.api.dto.AccountDto();
        accountDto.setNumber(accountEntity.getNumber());
        accountDto.setClient(clientDto);
        accountDto.setActive(accountEntity.isActive());
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
        if (clientEntity == null) {
            return null;
        }
        AccountEntity accountEntity = new AccountEntity();

        accountEntity.setNumber(accountDto.getNumber());
        accountEntity.setClient(clientEntity);
        accountEntity.setActive(accountDto.isActive());
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