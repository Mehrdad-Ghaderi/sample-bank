package com.mehrdad.sample.bank.domain.mapper;

import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.domain.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "sender.id", target = "senderAccountId")
    @Mapping(source = "receiver.id", target = "receiverAccountId")
    TransactionDto toTransactionDto(TransactionEntity transaction);

    List<TransactionDto> toTransactionDtoList(List<TransactionEntity> transactions);
}