package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    private final AccountMapper accountMapper;

    public ClientMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public ClientDto toClientDto(ClientEntity clientEntity) {
        if (clientEntity == null) {
            return null;
        }
        ClientDto clientDto = new ClientDto();
        clientDto.setId(clientEntity.getId());
        clientDto.setName(clientEntity.getName());
        clientDto.setPhoneNumber(clientEntity.getPhoneNumber());
        clientDto.setActive(clientEntity.isActive());
        clientDto.setAccounts(accountMapper.toAccountDtoList(clientEntity.getAccounts(), clientDto));

        return clientDto;
    }

    public ClientEntity toClientEntity(ClientDto clientDto) {
        if (clientDto == null) {
            return null;
        }
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(clientDto.getId());
        clientEntity.setName(clientDto.getName());
        clientEntity.setPhoneNumber(clientDto.getPhoneNumber());
        clientEntity.setActive(clientDto.isActive());
        clientEntity.setAccounts(accountMapper.toAccountEntityList(clientDto.getAccounts(), clientEntity));

        return clientEntity;
    }

}
