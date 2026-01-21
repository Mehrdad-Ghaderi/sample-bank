package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import com.mehrdad.sample.bank.core.exception.*;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
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


    public TransactionService(MoneyRepository moneyRepository, AccountRepository accountRepository,
                              MoneyMapper moneyMapper, AccountMapper accountMapper,
                              TransactionRepository transactionRepository,
                              TransactionMapper transactionMapper) {
        this.moneyRepository = moneyRepository;
        this.accountRepository = accountRepository;
        this.moneyMapper = moneyMapper;
        this.accountMapper = accountMapper;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Executes a money transfer from a sender account to a receiver account.
     *
     * <p>This method performs the following steps atomically:
     * <ul>
     *     <li>Withdraws the amount from the sender's account</li>
     *     <li>Changes the Money ID and account to match the receiver</li>
     *     <li>Deposits the amount to the receiver's account</li>
     *     <li>Saves the transaction in the transaction history</li>
     * </ul>
     * </p>
     *
     * @param sender   the sender account
     * @param receiver the receiver account
     * @param money    the money object containing the amount and currency
     * @return true if the transfer is successful
     * @throws Exception if withdrawal or deposit fails
     */
    @Transactional
    public boolean transfer(AccountDto sender, AccountDto receiver, MoneyDto money) {
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

    /**
     * Saves a transaction record between two accounts.
     *
     * @param sender   the sender account
     * @param receiver the receiver account
     * @param money    the money object containing the amount and currency
     */
    private void saveTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        TransactionEntity transaction = createTransaction(sender, receiver, money);
        transactionRepository.save(transaction);
        System.out.println(transaction);
    }

    /**
     * Creates a transaction entity from sender, receiver, and money details.
     *
     * @param sender   the sender account
     * @param receiver the receiver account
     * @param money    the money object
     * @return a new TransactionEntity
     */
    private TransactionEntity createTransaction(AccountDto sender, AccountDto receiver, MoneyDto money) {
        AccountEntity senderAccountEntity = getAccountEntity(sender);
        AccountEntity receiverAccountEntity = getAccountEntity(receiver);

        return new TransactionEntity(senderAccountEntity, receiverAccountEntity, money.getAmount(), money.getCurrency().toString());
    }

    /**
     * Updates the ID of a MoneyDto to match the receiver's account.
     *
     * <p>This is necessary after withdrawal so the same MoneyDto can be reused for deposit.</p>
     *
     * @param receiver the receiver account
     * @param money    the money object
     */
    private void changeMoneyIdAndAccount(AccountDto receiver, @NotNull MoneyDto money) {
        money.setId(receiver.getNumber() + money.getCurrency());
    }

    /**
     * Deposits money into an account. Optionally subtracts the amount from the bank's account.
     *
     * @param accountDto       the target account
     * @param moneyDto         the money object
     * @param subtractFromBank if true, subtracts the amount from the bank's account
     * @return true if deposit succeeds
     * @throws Exception if account is inactive or amount is invalid
     */
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

    /**
     * Withdraws money from an account. Optionally adds the amount to the bank's account.
     *
     * @param accountDto the source account
     * @param moneyDto   the money object
     * @param addToBank  if true, adds the amount to the bank's account
     */
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

    /**
     * Updates the bank's central account balance by adding or subtracting the specified amount.
     *
     * <p>If the bank's balance record does not exist, it will be initialized with a zero balance before
     * performing the operation.</p>
     *
     * @param moneyDto  the money object containing the amount and currency to adjust
     * @param operation the type of operation (ADD or SUBTRACT)
     */
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

    /**
     * Transfers a specified amount from a customer account to the bank's account and records the transaction.
     *
     * <p>This method is typically called when the bank needs to collect money (e.g. fees).</p>
     *
     * @param accountDto the customer account sending the money
     * @param moneyDto   the money object containing the amount and currency to deposit
     */
    private void addToBankAccount(AccountDto accountDto, MoneyDto moneyDto) {
        updateBankBalance(moneyDto, BankOperation.ADD);
        AccountDto bankAccountDto = accountMapper.toAccountDto(getBankAccount());
        saveTransaction(accountDto, bankAccountDto, moneyDto);
    }

    /**
     * Transfers a specified amount from the bank's account to a customer account and records the transaction.
     *
     * <p>This method is typically called when the bank disburses funds to a customer.</p>
     *
     * @param accountDto the customer account receiving the money
     * @param moneyDto   the money object containing the amount and currency to withdraw
     */
    private void subtractFromBankAccount(AccountDto accountDto, MoneyDto moneyDto) {
        updateBankBalance(moneyDto, BankOperation.SUBTRACT);
        AccountDto bankAccountDto = accountMapper.toAccountDto(getBankAccount());
        saveTransaction(bankAccountDto, accountDto, moneyDto);
    }

    /**
     * Retrieves the last N transactions for a specific account.
     *
     * @param account                 the account to retrieve transactions for
     * @param numOfLatestTransactions number of recent transactions to fetch
     * @return a list of transaction DTOs
     */
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
