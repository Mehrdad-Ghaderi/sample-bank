package com.mehrdad.sample.bank.core.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
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
public class CustomerEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(length = 10, unique = true, nullable = false)
    private String externalReference;

    @NotBlank
    @Size(max = 45, message = "Name cannot be longer than 45 characters")
    @Column(length = 45, nullable = false)
    private String name;

    @NotBlank
    @Size(max = 15, message = "Phone number cannot be longer than 15 characters")
    @Column(length = 15, unique = true, nullable = false)
    private String phoneNumber;

    @JsonManagedReference
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AccountEntity> accounts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @Override
    public String toString() {
        return String.format(
                "CustomerEntity{id=%s, name='%s', phoneNumber='%s', status=%s, createdAt=%s, updatedAt=%s}",
                id, name, phoneNumber, status, createdAt, updatedAt
        );
    }
}