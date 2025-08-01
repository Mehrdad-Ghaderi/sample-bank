package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private AccountEntity sender;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private AccountEntity receiver;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private LocalDateTime transactionTime;

    public TransactionEntity(AccountEntity sender, AccountEntity receiver, BigDecimal amount, String currency) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
        this.transactionTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Transaction: " + currency +amount +
                " from " + sender.getNumber() +
                " to " + receiver.getNumber() +
                " " +
                transactionTime.format(DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss"));
    }
}
