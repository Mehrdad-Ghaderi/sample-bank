package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import com.mehrdad.sample.bank.core.mapper.MoneyMapper;
import com.mehrdad.sample.bank.core.repository.MoneyRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class MoneyService {

    private final MoneyRepository moneyRepository;
    private final MoneyMapper moneyMapper;
    private final TransactionService transactionService;

    public MoneyService(MoneyRepository moneyRepository, MoneyMapper moneyMapper, TransactionService transactionService) {
        this.moneyRepository = moneyRepository;
        this.moneyMapper = moneyMapper;
        this.transactionService = transactionService;
    }

    public boolean depositMoney(MoneyDto moneyDto) {
        Optional<MoneyEntity> moneyEntity = getMoneyEntity(moneyDto);
        if (moneyEntity.isEmpty()) {
            return false;
        } else {
            addMoney(moneyDto, moneyEntity.get());
            moneyRepository.save(moneyEntity.get());
        }
        return true;
    }

    private BigDecimal addMoney(MoneyDto moneyDto, MoneyEntity moneyEntity) {
        BigDecimal add = moneyEntity.getAmount().add(moneyDto.getAmount());
        transactionService.saveTransaction();
        return add;
    }

    private Optional<MoneyEntity> getMoneyEntity(MoneyDto moneyDto) {
        return Optional.ofNullable(moneyMapper.toMoneyEntity(moneyDto));
    }
}
