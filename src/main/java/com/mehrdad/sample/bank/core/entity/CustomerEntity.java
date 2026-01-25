package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
@Table(
        name = "customer_entity",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_customer_business_id",
                        columnNames = "businessId"
                ),
                @UniqueConstraint(
                        name = "uk_customer_phone",
                        columnNames = "phoneNumber"
                )
        },
        indexes = {
                @Index(
                        name = "idx_customer_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    // Business-visible identifier
    @Column(nullable = false, updatable = false)
    private Integer businessId;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(length = 13, nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountEntity> accounts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public void addAccount(AccountEntity account) {
        accounts.add(account);
        account.setCustomer(this);
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