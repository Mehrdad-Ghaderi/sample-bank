package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDto toClientDto(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        ClientDto dto = new ClientDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setActive(entity.isActive());
        return dto;
    }

    public ClientEntity toClientEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }

        return new ClientEntity(
                dto.getId(),
                dto.getName(),
                dto.getPhoneNumber(),
                dto.isActive()
        );
    }

}
