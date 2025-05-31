package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
public class TransactionEntity {

    private Long id;
    private AccountEntity sender;
    private AccountEntity receiver;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime transactionTime;

    public TransactionEntity(AccountEntity sender, AccountEntity receiver, BigDecimal amount, String currency) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
        this.transactionTime = LocalDateTime.now();
    }

    public TransactionEntity() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    public AccountEntity getSender() {
        return sender;
    }

    public void setSender(AccountEntity sender) {
        this.sender = sender;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    public AccountEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountEntity receiver) {
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

    public void setTransactionTime(LocalDateTime localDateTime) {
        this.transactionTime = localDateTime;
    }

    @Override
    public String toString() {
        return "Transaction:\nfrom" +
                 sender +
                ", to " + receiver +
                ", " + amount + currency +
                transactionTime.format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss"));
    }

}
