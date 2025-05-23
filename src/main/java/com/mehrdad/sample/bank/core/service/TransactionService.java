package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
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

    @Transactional
    public boolean transfer(AccountDto sender, AccountDto receiver, MoneyDto money) throws Exception {
        if (withdraw(money, false)) {//first withdrawal is done, if true, then
            changeMoneyIdAndAccount(receiver, money);
            deposit(money, false);
            saveTransaction(sender, receiver, money);
            return true;
        } else {
            return false;
        }
    }

    private TransactionEntity createTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        ClientEntity senderClientEntity = clientMapper.toClientEntity(sender.getClient());
        AccountEntity senderAccountEntity = accountMapper.toAccountEntity(sender, senderClientEntity);

        ClientEntity receiverClientEntity = clientMapper.toClientEntity(receiver.getClient());
        AccountEntity receiverAccountEntity = accountMapper.toAccountEntity(receiver, receiverClientEntity);

        return new TransactionEntity(senderAccountEntity, receiverAccountEntity, money.getAmount(), money.getCurrency().toString());
    }

    private void saveTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        TransactionEntity transaction = createTransaction(sender, receiver, money);
        transactionRepository.save(transaction);
        notifyMembers(transaction);
    }

    /**
     * changes the account of MoneyDto after withdrawal from the sender account
     * in order for the same Money object to be used for deposit method, which needs a MoneyDto,
     * but with a different Id and Account.
     *
     * @param receiver
     * @param money
     */
    private void changeMoneyIdAndAccount(AccountDto receiver, @NotNull MoneyDto money) {
        money.setAccount(receiver);
        money.setId(receiver.getNumber() + money.getCurrency()); //this is how the Money id is implemented
    }

    @Transactional
    public boolean deposit(MoneyDto moneyDto, boolean subtractFromBank) throws Exception {
        AccountEntity foundAccountEntity = getAccountEntity(moneyDto.getAccount());
        assertAccountActiveStatus(foundAccountEntity);
        assertAmount(moneyDto);

        Optional<MoneyEntity> moneyEntity = moneyRepository.findById(moneyDto.getId());

        if (moneyEntity.isEmpty()) {
            MoneyEntity newMoney = moneyMapper.toMoneyEntity(moneyDto, foundAccountEntity);
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
    public boolean withdraw(MoneyDto moneyDto, boolean addToBank) {
        AccountEntity foundAccountEntity = getAccountEntity(moneyDto.getAccount());
        assertAccountActiveStatus(foundAccountEntity);
        assertAmount(moneyDto);

        Optional<MoneyEntity> moneyEntity = moneyRepository.findById(moneyDto.getId());

        if (moneyEntity.isEmpty()) {
            throw new MoneyNotFoundException(moneyMapper.toMoneyEntity(moneyDto, foundAccountEntity));
        } else {
            assertSufficientBalance(moneyEntity.get(), moneyDto);

            moneyEntity.get().setAmount(subtractAmount(moneyEntity.get(), moneyDto));
            moneyRepository.save(moneyEntity.get());
            if (addToBank) {
                addToBankAccount(moneyDto);
            }
        }
        return true;
    }

    public List<TransactionDto> getLastTransactions(AccountDto account, int numOfLatestTransactions) {
        return transactionRepository.findLastTransactions(account.getNumber(), numOfLatestTransactions)
                .parallelStream()
                .map(transactionMapper::toTransactionEntity)
                .collect(Collectors.toList());
    }

    private AccountEntity getAccountEntity(AccountDto accountDto) {
        return accountRepository.findById(accountDto.getNumber())
                .orElseThrow(() -> new AccountNotFoundException(accountDto.getNumber()));
    }

    private void addToBankAccount(MoneyDto moneyDto) {
        AccountEntity bankAccount = getBankAccount();

        Optional<MoneyEntity> foundBankMoney = moneyRepository.findById(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());

        if (foundBankMoney.isEmpty()) {
            MoneyEntity bankMoneyEntity = moneyMapper.toMoneyEntity(moneyDto, bankAccount);
            bankMoneyEntity.setAmount(moneyDto.getAmount());
            bankMoneyEntity.setId(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());
            bankMoneyEntity.setAccount(bankAccount);
            moneyRepository.save(bankMoneyEntity);
        } else {
            foundBankMoney.get().setAmount(foundBankMoney.get().getAmount().add(moneyDto.getAmount()));
            moneyRepository.save(foundBankMoney.get());
        }

        assert bankAccount != null;
        ClientDto bank = clientMapper.toClientDto(bankAccount.getClient());
        AccountDto bankAccountDto = accountMapper.toAccountDto(bankAccount, bank);
        saveTransaction(moneyDto.getAccount(), bankAccountDto, moneyDto);
    }

    private void subtractFromBankAccount(MoneyDto moneyDto) {
        AccountEntity bankAccount = getBankAccount();

        Optional<MoneyEntity> foundBankMoney = moneyRepository.findById(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());

        if (foundBankMoney.isEmpty()) {
            MoneyEntity bankMoneyEntity = moneyMapper.toMoneyEntity(moneyDto, bankAccount);
            bankMoneyEntity.setAmount(moneyDto.getAmount().negate());
            bankMoneyEntity.setId(BANK_ACCOUNT_NUMBER + moneyDto.getCurrency());
            bankMoneyEntity.setAccount(bankAccount);
            moneyRepository.save(bankMoneyEntity);
        } else {
            foundBankMoney.get().setAmount(foundBankMoney.get().getAmount().subtract(moneyDto.getAmount()));
            moneyRepository.save(foundBankMoney.get());
        }

        assert bankAccount != null;
        ClientDto bank = clientMapper.toClientDto(bankAccount.getClient());
        AccountDto bankAccountDto = accountMapper.toAccountDto(bankAccount, bank);
        saveTransaction(bankAccountDto, moneyDto.getAccount(), moneyDto);
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
        if (!account.isActive()) {
            System.out.println("Account number " + account.getNumber() + " is inactive.");
            throw new AccountInactiveException(account.getNumber());
        }
    }

    private void assertSufficientBalance(MoneyEntity moneyEntity, MoneyDto moneyDto) {
        if (moneyEntity.getAmount().compareTo(moneyDto.getAmount()) < 0) {
            throw new InsufficientBalanceException(moneyEntity);
        }
    }

    private void assertAmount(MoneyDto moneyDto) {
        if (moneyDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Negative or zero amounts cannot be deposited.");
            throw new InvalidAmountException(moneyDto.getAmount());
        }
    }

    private void notifyMembers(TransactionEntity transaction) {
        if (transaction.getSender().isActive()) {
            System.out.println(transaction.toString());
        }
        if (transaction.getReceiver().isActive()) {
            System.out.println(transaction.toString());
        }
    }

}
