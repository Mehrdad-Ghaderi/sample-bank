package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface TransactionMapper {
    @Mapping(source = "balance.amount", target = "amount")
    @Mapping(source = "balance.currency", target = "currency")
    TransactionDto toTransactionDto(TransactionEntity transaction);

    List<TransactionDto> toTransactionDtoList(List<TransactionEntity> transactions);
}