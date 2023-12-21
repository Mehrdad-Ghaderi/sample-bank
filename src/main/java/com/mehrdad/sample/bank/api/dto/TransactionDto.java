package com.mehrdad.sample.bank.api.dto;

import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.api.dto.textservice.Event;
import com.mehrdad.sample.bank.api.dto.textservice.EventListener;
import com.mehrdad.sample.bank.api.dto.textservice.Publisher;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionDto implements Publisher {

    private Long id;
    private AccountDto sender;
    private AccountDto receiver;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime transactionTime;

    private List<EventListener> listeners;

    public TransactionDto(AccountDto sender, AccountDto receiver, BigDecimal amount, String currency) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
        this.transactionTime = LocalDateTime.now();
        listeners = new ArrayList<>(Arrays.asList(sender, receiver));

        notifyListeners();
    }

    public TransactionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public AccountDto getSender() {
        return sender;
    }

    public void setSender(AccountDto sender) {
        this.sender = sender;
    }

    @NotNull
    public AccountDto getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountDto receiver) {
        this.receiver = receiver;
    }

    @NotNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @NotNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @NotNull
    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                ", Sender=" + sender.getClient().getName() + " " + sender.getNumber() +
                ", Receiver=" + receiver.getClient().getName() + " " + receiver.getNumber() +
                ", Amount=" + amount + currency +
                ", Time=" + transactionTime.format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss")) +
                '}';
    }

    @Override
    public void registerListener(EventListener eventListener) {
        if (!listeners.contains(eventListener)) {
            listeners.add(eventListener);
        }
    }

    @Override
    public void removeListener(EventListener eventListener) {
        if (listeners.contains(eventListener)) {
            listeners.remove(eventListener);
        } else {
            System.out.println();
        }
    }

    @Override
    public void notifyListeners() {
        var event = new Event(toString());
        for (EventListener e : listeners) {
            e.onEvent(event);
        }
    }
}
