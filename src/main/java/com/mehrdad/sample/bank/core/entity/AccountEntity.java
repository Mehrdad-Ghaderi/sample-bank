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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    // Business identifier (IBAN / account number)
    @Column(length = 15, unique = true, nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id", nullable = false)
    private List<MoneyEntity> moneys = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;


    public Optional<MoneyEntity> getMoney(Currency currency) {
        return moneys.stream()
                .filter(entity -> entity.getCurrency() == currency)
                .findFirst();
    }

    public MoneyEntity getOrCreateMoney(Currency currency) {
        return getMoney(currency).orElseGet(() -> {
            MoneyEntity money = new MoneyEntity(currency, BigDecimal.ZERO);
            moneys.add(money);
            return money;
        });
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
