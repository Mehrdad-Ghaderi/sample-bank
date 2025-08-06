package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import com.mehrdad.sample.bank.core.exception.*;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.mapper.MoneyMapper;
import com.mehrdad.sample.bank.core.mapper.TransactionMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.MoneyRepository;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
public class TransactionService {


    private enum BankOperation {
        ADD,
        SUBTRACT
    }


    private static final String BANK_ACCOUNT_NUMBER = "111.1";

    private final MoneyRepository moneyRepository;
    private final MoneyMapper moneyMapper;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ClientMapper clientMapper;
    private final AccountService accountService;

    public TransactionService(MoneyRepository moneyRepository, AccountRepository accountRepository,
                              MoneyMapper moneyMapper, AccountMapper accountMapper,
                              TransactionRepository transactionRepository,
                              TransactionMapper transactionMapper,
                              ClientMapper clientMapper, AccountService accountService) {
        this.moneyRepository = moneyRepository;
        this.accountRepository = accountRepository;
        this.moneyMapper = moneyMapper;
        this.accountMapper = accountMapper;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.clientMapper = clientMapper;
        this.accountService = accountService;
    }

    @Transactional
    public boolean transfer(AccountDto sender, AccountDto receiver, MoneyDto money) throws Exception {
        try {
            withdraw(sender, money, false);
            changeMoneyIdAndAccount(receiver, money);
            deposit(receiver, money, false);
            saveTransaction(sender, receiver, money);
            return true;
        } catch (MoneyNotFoundException | IllegalStateException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Transfer failed due to internal error", e);
        }
    }

    private void saveTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        TransactionEntity transaction = createTransaction(sender, receiver, money);
        transactionRepository.save(transaction);
        System.out.println(transaction);
    }

    private TransactionEntity createTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        AccountEntity senderAccountEntity = getAccountEntity(sender);
        AccountEntity receiverAccountEntity = getAccountEntity(receiver);

        return new TransactionEntity(senderAccountEntity, receiverAccountEntity, money.getAmount(), money.getCurrency().toString());
    }

    /**
     * changes the account of MoneyDto after withdrawal from the sender account
     * in order for the same Money object to be used for deposit method, which needs a MoneyDto,
     * but with a different ID and Account.
     */
    private void changeMoneyIdAndAccount(AccountDto receiver, @NotNull MoneyDto money) {
        money.setId(receiver.getNumber() + money.getCurrency());
    }

    @Transactional
    public boolean deposit(AccountDto accountDto, MoneyDto moneyDto, boolean subtractFromBank) throws Exception {
        AccountEntity foundAccountEntity = getAccountEntity(accountDto);
        assertAccountActiveStatus(foundAccountEntity);
        assertAmount(moneyDto);

        Optional<MoneyEntity> moneyEntity = moneyRepository.findById(moneyDto.getId());

        if (moneyEntity.isEmpty()) {
            MoneyEntity newMoney = moneyMapper.toMoneyEntity(moneyDto);
            moneyRepository.save(newMoney);
        } else {
            moneyEntity.get().setAmount(addAmount(moneyEntity.get(), moneyDto));
            moneyRepository.save(moneyEntity.get());
        }
        if (subtractFromBank) {
            subtractFromBankAccount(accountDto, moneyDto);
        }
        return true;
    }

    @Transactional
    public void withdraw(AccountDto accountDto, MoneyDto moneyDto, boolean addToBank) {
        AccountEntity foundAccountEntity = getAccountEntity(accountDto);
        assertAccountActiveStatus(foundAccountEntity);
        assertAmount(moneyDto);

        MoneyEntity moneyEntity = moneyRepository.findById(moneyDto.getId())
                .orElseThrow(() -> new MoneyNotFoundException("Money ID: " + moneyDto.getId() + " not found!"));

        assertSufficientBalance(foundAccountEntity, moneyEntity, moneyDto);

        moneyEntity.setAmount(subtractAmount(moneyEntity, moneyDto));
        moneyRepository.save(moneyEntity);

        if (addToBank) {
            addToBankAccount(accountDto, moneyDto);
        }
    }

    private void updateBankBalance(MoneyDto moneyDto, BankOperation operation) {
        String bankMoneyId = BANK_ACCOUNT_NUMBER + moneyDto.getCurrency();
        Optional<MoneyEntity> foundBankMoney = moneyRepository.findById(bankMoneyId);

        MoneyEntity bankMoneyEntity = foundBankMoney.orElseGet(() -> {
            MoneyEntity newMoney = moneyMapper.toMoneyEntity(moneyDto);
            newMoney.setId(bankMoneyId);
            newMoney.setAmount(BigDecimal.ZERO);
            return newMoney;
        });

        if (operation == BankOperation.ADD) {
            bankMoneyEntity.setAmount(bankMoneyEntity.getAmount().add(moneyDto.getAmount()));
        } else {
            bankMoneyEntity.setAmount(bankMoneyEntity.getAmount().subtract(moneyDto.getAmount()));
        }

        moneyRepository.save(bankMoneyEntity);
    }

    private void addToBankAccount(AccountDto accountDto, MoneyDto moneyDto) {
        updateBankBalance(moneyDto, BankOperation.ADD);
        AccountDto bankAccountDto = accountMapper.toAccountDto(getBankAccount());
        saveTransaction(accountDto, bankAccountDto, moneyDto);
    }

    private void subtractFromBankAccount(AccountDto accountDto, MoneyDto moneyDto) {
        updateBankBalance(moneyDto, BankOperation.SUBTRACT);
        AccountDto bankAccountDto = accountMapper.toAccountDto(getBankAccount());
        saveTransaction(bankAccountDto, accountDto, moneyDto);
    }



    public List<TransactionDto> getLastTransactions(AccountDto account, int numOfLatestTransactions) {
        return transactionRepository.findLastTransactions(account.getNumber(), numOfLatestTransactions)
                .parallelStream()
                .map(transactionMapper::toTransactionDto)
                .collect(Collectors.toList());
    }

    private AccountEntity getAccountEntity(AccountDto accountDto) {
        return accountRepository.findById(accountDto.getNumber())
                .orElseThrow(() -> new AccountNotFoundException(accountDto.getNumber()));
    }

    private AccountEntity getBankAccount() {
        return accountRepository.findById(BANK_ACCOUNT_NUMBER)
                .orElseThrow(() -> new AccountNotFoundException(BANK_ACCOUNT_NUMBER));
    }

    private BigDecimal addAmount(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        return moneyEntity.getAmount().add(moneyDto.getAmount());
    }

    private BigDecimal subtractAmount(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        return moneyEntity.getAmount().subtract(moneyDto.getAmount());
    }


    private void assertAccountActiveStatus(AccountEntity account) {
        if (!account.getActive()) {
            System.out.println("Account number " + account.getNumber() + " is inactive.");
            throw new AccountInactiveException(account.getNumber());
        }
    }

    private void assertSufficientBalance(AccountEntity accountEntity, MoneyEntity moneyEntity, MoneyDto moneyDto) {
        if (moneyEntity.getAmount().compareTo(moneyDto.getAmount()) < 0) {
            throw new InsufficientBalanceException(accountEntity, moneyEntity);
        }
    }

    private void assertAmount(MoneyDto moneyDto) {
        if (moneyDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Negative or zero amounts cannot be deposited.");
            throw new InvalidAmountException(moneyDto.getAmount());
        }
    }
}
