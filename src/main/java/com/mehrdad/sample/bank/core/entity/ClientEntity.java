package com.mehrdad.sample.bank.core.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
public class ClientEntity {

    @Id
    @NotBlank
    @Size(max = 10, message = "ID cannot be longer than 10 characters")
    @Column(length = 10, unique = true, nullable = false)
    private String id;

    @NotBlank
    @Size(max = 45, message = "Name cannot be longer than 45 characters")
    @Column(length = 45, nullable = false)
    private String name;

    @NotBlank
    @Size(max = 15, message = "Phone number cannot be longer than 15 characters")
    @Column(length = 15, unique = true, nullable = false)
    private String phoneNumber;

    @JsonManagedReference
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountEntity> accounts;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;


    public ClientEntity(String id, String name, String phoneNumber, Boolean status) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.active = status;
    }

    @Override
    public String toString() {
        return String.format("ClientEntity{id='%s', name='%s', phoneNumber='%s', active=%s, accounts=%s}",
                id, name, phoneNumber, active, accounts);
    }
}