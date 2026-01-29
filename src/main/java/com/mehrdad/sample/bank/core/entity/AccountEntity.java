package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
@Table(
        name = "account_entity",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_number",
                        columnNames = "number"
                )
        },
        indexes = {
                @Index(name = "idx_account_customer", columnList = "customer_id"),
                @Index(name = "idx_account_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    // Business identifier (year-bankCode-customerBusinessId number)
    @Column(length = 19, unique = true, nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ElementCollection
    @CollectionTable(
            name = "account_balance",
            joinColumns = @JoinColumn(name = "account_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "currency")
    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private Map<Currency, BigDecimal> balances = new HashMap<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;


    public BigDecimal getBalance(Currency currency) {
        return balances.getOrDefault(currency, BigDecimal.ZERO);
    }

    public void increaseBalance(Currency currency, BigDecimal amount) {
        balances.merge(currency, amount, BigDecimal::add);
    }

    public void decreaseBalance(Currency currency, BigDecimal amount) {
        BigDecimal current = balances.getOrDefault(currency, BigDecimal.ZERO);
        if (current.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        balances.put(currency, current.subtract(amount));
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
