package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    boolean existsByName(String name);

    Optional<CustomerEntity> findByName(String defaultCustomerName);
}