package com.mehrdad.sample.bank.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @NotBlank
    @Size(max = 10, message = "Account number cannot be longer than 10 characters")
    private String number;

    @JsonBackReference
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(/*mappedBy = "account", */fetch = FetchType.EAGER)
    @JoinColumn(name = "account_number")
    private List<MoneyEntity> moneys;

    @Override
    public String toString() {
        return String.format("%s , active=%s}",
                number, status);
    }

}
