package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.mapper.MoneyMapper;
import com.mehrdad.sample.bank.core.mapper.TransactionMapper;
import com.mehrdad.sample.bank.core.repository.AccountRepository;
import com.mehrdad.sample.bank.core.repository.MoneyRepository;
import com.mehrdad.sample.bank.core.repository.TransactionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final String BANK_ACCOUNT_NUMBER = "111.1";

    private final MoneyRepository moneyRepository;
    private final MoneyMapper moneyMapper;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ClientMapper clientMapper;

    public TransactionService(MoneyRepository moneyRepository, AccountRepository accountRepository, MoneyMapper moneyMapper, AccountMapper accountMapper, TransactionRepository transactionRepository, TransactionMapper transactionMapper, ClientMapper clientMapper) {
        this.moneyRepository = moneyRepository;
        this.accountRepository = accountRepository;
        this.moneyMapper = moneyMapper;
        this.accountMapper = accountMapper;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.clientMapper = clientMapper;
    }

    private void saveTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        TransactionEntity transaction = createTransaction(sender, receiver, money);
        transactionRepository.save(transaction);
    }

    private TransactionEntity createTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        ClientEntity senderClientEntity = clientMapper.toClientEntity(sender.getClient());
        AccountEntity senderAccountEntity = accountMapper.toAccountEntity(sender, senderClientEntity);

        ClientEntity receiverClientEntity = clientMapper.toClientEntity(receiver.getClient());
        AccountEntity receiverAccountEntity = accountMapper.toAccountEntity(receiver, receiverClientEntity);

        MoneyEntity moneyEntity = moneyMapper.toMoneyEntity(money);

        return new TransactionEntity(senderAccountEntity, receiverAccountEntity, moneyEntity.getAmount(), moneyEntity.getCurrency().toString());
    }

    @Transactional
    public boolean transfer(AccountDto sender, AccountDto receiver, MoneyDto money) {
        if (withdraw(money, false)) {
            changeMoneyIdAndAccount(receiver, money);
            deposit(money, false);
            saveTransaction(sender, receiver, money);
            return true;
        } else {
            return false;
        }
    }

    /**
     * changes the account of MoneyDto after withdrawal from the sender account
     * in order for the same object to be used for deposit method, which needs a MoneyDto,
     * but with a different Id and Account.
     *
     * @param receiver
     * @param money
     */
    private void changeMoneyIdAndAccount(AccountDto receiver, @NotNull MoneyDto money) {
        money.setAccount(receiver);
        money.setId(receiver.getNumber() + money.getCurrency());
    }

    @Transactional
    public boolean deposit(MoneyDto moneyDto, boolean subtractFromBank) {
        if (invalidAmount(moneyDto)) return false;

        if (accountInactive(moneyDto.getAccount())) {
            return false;
        }

        Optional<MoneyEntity> moneyEntity = moneyRepository.findById(moneyDto.getId());
        if (moneyEntity.isEmpty()) {
            MoneyEntity newMoney = moneyMapper.toMoneyEntity(moneyDto);
            moneyRepository.save(newMoney);
        } else {
            moneyEntity.get().setAmount(addAmount(moneyEntity.get(), moneyDto));
            moneyRepository.save(moneyEntity.get());
        }
        if (subtractFromBank) {
            subtractFromBankAccount(moneyDto);
        }
        return true;
    }

    @Transactional
    public boolean withdraw(MoneyDto moneyDto, boolean subtractFromBank) {
        if (invalidAmount(moneyDto)) return false;

        if (accountInactive(moneyDto.getAccount())) {
            return false;
        }

        Optional<MoneyEntity> moneyEntity = moneyRepository.findById(moneyDto.getId());
        if (moneyEntity.isEmpty()) {
            System.out.println("There is no " + moneyDto.getCurrency() + " in account number " + moneyDto.getAccount().getNumber());
            return false;
        } else {
            if (sufficientBalance(moneyEntity.get(), moneyDto)) {
                moneyEntity.get().setAmount(subtractAmount(moneyEntity.get(), moneyDto));
                moneyRepository.save(moneyEntity.get());
                if (subtractFromBank) {
                    addToBankAccount(moneyDto);
                }
                return true;
            }
        }
        return false;
    }

    private boolean accountInactive(@javax.validation.constraints.NotNull AccountDto senderAccount) {
        if (!senderAccount.isActive()) {
            System.out.println("Account number " + senderAccount.getNumber() + " is inactive.");
            return true;
        }
        return false;
    }

    private boolean sufficientBalance(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        if (moneyEntity.getAmount().compareTo(moneyDto.getAmount()) >= 0) {
            return true;
        }
        System.out.println("There is not sufficient amount of " + moneyDto.getCurrency() + "in account number " + moneyEntity.getAccount().getNumber());
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
        Optional<AccountEntity> foundBankAccount = accountRepository.findById(BANK_ACCOUNT_NUMBER);
        if (foundBankAccount.isEmpty()) {
            System.out.println("The main bank account was not found.");
            return;
        }

        AccountEntity bankAccount = foundBankAccount.get();

        Optional<MoneyEntity> foundBankMoney = moneyRepository.findById(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());

        if (foundBankMoney.isEmpty()) {
            MoneyEntity bankMoneyEntity = moneyMapper.toMoneyEntity(moneyDto);

            bankMoneyEntity.setAmount(moneyDto.getAmount());
            bankMoneyEntity.setId(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());
            bankMoneyEntity.setAccount(bankAccount);
            moneyRepository.save(bankMoneyEntity);
        } else {
            foundBankMoney.get().setAmount(foundBankMoney.get().getAmount().add(moneyDto.getAmount()));
            moneyRepository.save(foundBankMoney.get());
        }

        ClientDto bank = clientMapper.toClientDto(bankAccount.getClient());
        AccountDto bankAccountDto = accountMapper.toAccountDto(bankAccount, bank);
        saveTransaction(moneyDto.getAccount(), bankAccountDto, moneyDto);
    }

    private void subtractFromBankAccount(MoneyDto moneyDto) {
        Optional<AccountEntity> foundBankAccount = accountRepository.findById(BANK_ACCOUNT_NUMBER);
        if (foundBankAccount.isEmpty()) {
            System.out.println("The main bank account was not found.");
            return;
        }

        AccountEntity bankAccount = foundBankAccount.get();

        Optional<MoneyEntity> foundBankMoney = moneyRepository.findById(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());

        if (foundBankMoney.isEmpty()) {
            MoneyEntity bankMoneyEntity = moneyMapper.toMoneyEntity(moneyDto);
            bankMoneyEntity.setAmount(moneyDto.getAmount().negate());
            bankMoneyEntity.setId(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());
            bankMoneyEntity.setAccount(bankAccount);
            moneyRepository.save(bankMoneyEntity);

        } else {
            foundBankMoney.get().setAmount(foundBankMoney.get().getAmount().subtract(moneyDto.getAmount()));
            moneyRepository.save(foundBankMoney.get());
        }

        ClientDto bank = clientMapper.toClientDto(bankAccount.getClient());
        AccountDto bankAccountDto = accountMapper.toAccountDto(bankAccount, bank);
        saveTransaction(bankAccountDto, moneyDto.getAccount(), moneyDto);
    }

    private BigDecimal addAmount(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        return moneyEntity.getAmount().add(moneyDto.getAmount());
    }

    private BigDecimal subtractAmount(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        return moneyEntity.getAmount().subtract(moneyDto.getAmount());
    }


    public List<TransactionDto> getLastTransactions(AccountDto account, int numOfLatestTransactions) {
        return transactionRepository.findLastTransactions(account.getNumber(), numOfLatestTransactions)
                .parallelStream()
                .map(transactionMapper::toTransactionEntity)
                .collect(Collectors.toList());
    }

    private Predicate<TransactionEntity> isSender(AccountDto account) {
        return transaction -> transaction.getSender().getNumber().equals(account.getNumber());
    }

    private Predicate<TransactionEntity> isReceiver(AccountDto account) {
        return transaction -> transaction.getReceiver().getNumber().equals(account.getNumber());
    }

}
