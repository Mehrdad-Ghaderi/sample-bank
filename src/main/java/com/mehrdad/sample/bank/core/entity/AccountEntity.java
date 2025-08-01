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
    @NotBlank
    @Size(max = 10, message = "Account number cannot be longer than 10 characters")
    private String number;

    @JsonBackReference
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private ClientEntity client;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(/*mappedBy = "account", */fetch = FetchType.EAGER)
    @JoinColumn(name = "account_number")
    private List<MoneyEntity> moneys;

    public AccountEntity(String number, ClientEntity client, Boolean status) {
        this.number = number;
        this.client = client;
        this.active = status;
    }






    @Override
    public String toString() {
        return String.format("%s , active=%s}",
                number, active);
    }

}
