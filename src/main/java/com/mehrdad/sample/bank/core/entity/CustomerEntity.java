package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Pattern;
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
                        columnNames = "business_id"
                ),
                @UniqueConstraint(
                        name = "uk_customer_phone",
                        columnNames = "phone_number"
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
    @Column(name = "business_id", nullable = false, updatable = false)
    private Integer businessId;

    @Column(length = 100, nullable = false)
    private String name;

    @Pattern(
            regexp = "^\\+[0-9]{10,16}$",
            message = "Phone number must be stored in international format"
    )
    @Column(name = "phone_number", length = 16, nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountEntity> accounts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
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

        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}