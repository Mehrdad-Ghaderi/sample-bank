package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    private final AccountMapper accountMapper;
    private final ClientMapper clientMapper;

    public TransactionMapper(AccountMapper accountMapper, ClientMapper clientMapper) {

        this.accountMapper = accountMapper;
        this.clientMapper = clientMapper;
    }

    public TransactionDto toTransactionEntity(TransactionEntity transaction) {
        if (transaction == null) {
            return null;
        }

        ClientDto sender = clientMapper.toClientDto(transaction.getSender().getClient());
        ClientDto receiver = clientMapper.toClientDto(transaction.getReceiver().getClient());

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setSender(accountMapper.toAccountDto(transaction.getSender(), sender));
        transactionDto.setReceiver(accountMapper.toAccountDto(transaction.getReceiver(), receiver));
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setCurrency(transaction.getCurrency());
        transactionDto.setTransactionTime(transaction.getTransactionTime());

        return transactionDto;
    }

}
