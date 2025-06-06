package com.mehrdad.sample.bank.core.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
@Entity
public class ClientEntity {

    private String id;
    private String name;
    private String phoneNumber;

    @JsonManagedReference
    private List<AccountEntity> accounts;
    private Boolean active;

    public ClientEntity() {
    }

    public ClientEntity(String id, String name, String phoneNumber, Boolean status) {
        this.id = id; //has to be validated for uniqueness
        this.name = name;
        this.phoneNumber = phoneNumber; // has to be validated for uniqueness
        this.active = status;
    }

    @Id
    @NotBlank
    @Size(max = 10)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotBlank
    @Size(max = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank
    @Size(max = 15)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    public List<AccountEntity> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountEntity> accounts) {
        this.accounts = accounts;
    }

    @NotNull
    @Column(name = "active", nullable = false)
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean status) {
        this.active = status;
    }

    @Override
    public String toString() {
        return String.format("ClientEntity{id='%s', name='%s', phoneNumber='%s', active=%s, accounts=%s}",
                id, name, phoneNumber, active, accounts);
    }
}