package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import com.mehrdad.sample.bank.core.mapper.MoneyMapper;
import com.mehrdad.sample.bank.core.repository.MoneyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {

    private final MoneyRepository moneyRepository;
    private final MoneyMapper moneyMapper;

    public TransactionService(MoneyRepository moneyRepository, MoneyMapper moneyMapper) {
        this.moneyRepository = moneyRepository;
        this.moneyMapper = moneyMapper;
    }

    public void transfer(AccountDto sender, AccountDto receiver, MoneyDto money) {

    }

    public boolean depositMoney(MoneyDto moneyDto) {

        Optional<MoneyEntity> moneyEntity = Optional.of(moneyRepository.getById(moneyDto.getId()));
        if (moneyEntity.isEmpty()) {
            moneyRepository.save(moneyMapper.toMoneyEntity(moneyDto));

        } else {
            depositMoney(moneyDto, moneyEntity.get());
            moneyRepository.save(moneyEntity.get());
        }
        return true;
    }

    private BigDecimal depositMoney(MoneyDto moneyDto, MoneyEntity moneyEntity) {
        BigDecimal addedMoney = moneyEntity.getAmount().add(moneyDto.getAmount());
        //  transactionService.saveTransaction();
        return addedMoney;
    }

}
