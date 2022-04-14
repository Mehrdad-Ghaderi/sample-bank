package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.MoneyMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.MoneyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {

    private final MoneyRepository moneyRepository;
    private final AccountRepository accountRepository;
    private final MoneyMapper moneyMapper;
    private final AccountMapper accountMapper;

    public TransactionService(MoneyRepository moneyRepository, AccountRepository accountRepository, MoneyMapper moneyMapper, AccountMapper accountMapper) {
        this.moneyRepository = moneyRepository;
        this.accountRepository = accountRepository;
        this.moneyMapper = moneyMapper;
        this.accountMapper = accountMapper;
    }

    private void saveTransaction(AccountDto sender, AccountDto Receiver, MoneyDto money) {


    }

    public void transfer(AccountDto sender, AccountDto receiver, MoneyDto money) {

    }

    public boolean deposit(MoneyDto moneyDto) {
        if (invalidAmount(moneyDto)) return false;

        Optional<MoneyEntity> moneyEntity = moneyRepository.findById(moneyDto.getId());
        if (moneyEntity.isEmpty()) {
            MoneyEntity newMoney = moneyMapper.toMoneyEntity(moneyDto);
            moneyRepository.save(newMoney);
            subtractFromBankAccount(moneyDto);
        } else {
            moneyEntity.get().setAmount(addAmount(moneyEntity.get(), moneyDto));
            moneyRepository.save(moneyEntity.get());
            subtractFromBankAccount(moneyDto);
        }
        return true;
    }

    public boolean withdraw(MoneyDto moneyDto) {
        if (invalidAmount(moneyDto)) return false;

        Optional<MoneyEntity> moneyEntity = moneyRepository.findById(moneyDto.getId());
        if (moneyEntity.isEmpty()) {
            return false;
        } else {
            if (sufficientBalance(moneyEntity.get(), moneyDto)) {
                moneyEntity.get().setAmount(subtractAmount(moneyEntity.get(), moneyDto));
                moneyRepository.save(moneyEntity.get());
                addToBankAccount(moneyDto);
                return true;
            }
        }
        return false;
    }

    private boolean sufficientBalance(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        if (moneyEntity.getAmount().compareTo(moneyDto.getAmount()) >= 0) {
            return true;
        }
        System.out.println("There is not sufficient balance in account number " + moneyEntity.getAccount().getNumber() );
        return false;
    }

    private boolean invalidAmount(MoneyDto moneyDto) {
        if (moneyDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Negative or zero amounts cannot be deposited.");
            return true;
        }
        return false;
    }

    private void addToBankAccount(MoneyDto moneyDto) {
        Optional<MoneyEntity> foundMoney = moneyRepository.findById("111.1" + moneyDto.getCurrency());

        if (foundMoney.isEmpty()) {
            Optional<AccountEntity> bankAccount = accountRepository.findById("111.1");
            MoneyEntity bankMoneyEntity = moneyMapper.toMoneyEntity(moneyDto);

            if (bankAccount.isPresent()) {
                bankMoneyEntity.setAmount(moneyDto.getAmount());
                bankMoneyEntity.setId("111.1" + moneyDto.getCurrency());
                bankMoneyEntity.setAccount(bankAccount.get());
                moneyRepository.save(bankMoneyEntity);
            } else {
                System.out.println("The main bank account was not found.");
            }
        } else {
            foundMoney.get().setAmount(foundMoney.get().getAmount().add(moneyDto.getAmount()));
            moneyRepository.save(foundMoney.get());
        }


    }

    private void subtractFromBankAccount(MoneyDto moneyDto) {
        Optional<MoneyEntity> foundMoney = moneyRepository.findById("111.1" + moneyDto.getCurrency());

        if (foundMoney.isEmpty()) {
            Optional<AccountEntity> bankAccount = accountRepository.findById("111.1");
            MoneyEntity bankMoneyEntity = moneyMapper.toMoneyEntity(moneyDto);

            if (bankAccount.isPresent()) {
                bankMoneyEntity.setAmount(moneyDto.getAmount().negate());
                bankMoneyEntity.setId("111.1" + moneyDto.getCurrency());
                bankMoneyEntity.setAccount(bankAccount.get());
                moneyRepository.save(bankMoneyEntity);
            } else {
                System.out.println("The main bank account was not found.");
            }
        } else {
            foundMoney.get().setAmount(foundMoney.get().getAmount().subtract(moneyDto.getAmount()));
            moneyRepository.save(foundMoney.get());
        }

    }

    private BigDecimal addAmount(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        return moneyEntity.getAmount().add(moneyDto.getAmount());
    }

    private BigDecimal subtractAmount(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        return moneyEntity.getAmount().subtract(moneyDto.getAmount());
    }

}
