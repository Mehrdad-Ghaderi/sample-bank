package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import org.springframework.stereotype.Component;

/**
 * Created by Mehrdad Ghaderi
 */
@Component
public class TransactionMapper {

    private final AccountMapper accountMapper;

    public TransactionMapper(AccountMapper accountMapper) {

        this.accountMapper = accountMapper;
    }

    public TransactionDto toTransactionEntity(TransactionEntity transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setSender(accountMapper.toAccountDto(transaction.getSender()));
        transactionDto.setReceiver(accountMapper.toAccountDto(transaction.getReceiver()));
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setCurrency(transaction.getCurrency());
        transactionDto.setTransactionTime(transaction.getTransactionTime());

        return transactionDto;
    }

}
