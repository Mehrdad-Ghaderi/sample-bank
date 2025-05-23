package com.mehrdad.sample.bank.core.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
public class AccountEntity {

    private String number;
    private ClientEntity client;
    private Boolean active;
    private List<MoneyEntity> moneys;

    public AccountEntity() {
    }

    public AccountEntity(String number, ClientEntity client, Boolean status) {
        this.number = number;
        this.client = client;
        this.active = status;
    }


    @Id
    @NotBlank
    @Size(max = 10)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    public List<MoneyEntity> getMoneys() {
        return moneys;
    }

    public void setMoneys(List<MoneyEntity> moneys) {
        this.moneys = moneys;
    }

    @NotNull
    @Column(name = "active", columnDefinition = "BIT default 1", length = 1)
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("AccountEntity{number='%s', clientId=%s, active=%s}",
                number, client != null ? client.getId() : null, active);
    }

}
