package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Component
public class MoneyMapper {

    public MoneyMapper() {
    }

    public MoneyEntity toMoneyEntity(MoneyDto moneyDto) {
        if (moneyDto == null) {
            return null;
        }

        MoneyEntity moneyEntity = new MoneyEntity();
        moneyEntity.setId(moneyDto.getId());
        moneyEntity.setCurrency(moneyDto.getCurrency());
        moneyEntity.setAmount(moneyDto.getAmount());

        return moneyEntity;
    }

    public MoneyDto toMoneyDto(MoneyEntity moneyEntity) {
        if (moneyEntity == null) {
            return null;
        }

        MoneyDto moneyDto = new MoneyDto();
        moneyDto.setId(moneyEntity.getId());
        moneyDto.setCurrency(moneyEntity.getCurrency());
        moneyDto.setAmount(moneyEntity.getAmount());

        return moneyDto;
    }

    public List<MoneyDto> toMoneyDtoList(List<MoneyEntity> moneyEntities) {
        if (moneyEntities == null) {
            return null;
        }
        return moneyEntities.parallelStream()
                .map(this::toMoneyDto)
                .collect(Collectors.toList());
    }

    public List<MoneyEntity> toMoneyEntityList(List<MoneyDto> moneyDtos) {
        if (moneyDtos == null) {
            return null;
        }
        return moneyDtos.parallelStream()
                .map(this::toMoneyEntity)
                .collect(Collectors.toList());
    }
}
