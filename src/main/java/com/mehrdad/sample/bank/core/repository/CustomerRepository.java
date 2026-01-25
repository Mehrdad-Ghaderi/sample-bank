package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    boolean existsByName(String name);

    Optional<CustomerEntity> findByName(String defaultCustomerName);

    @Query("select max(c.businessId) from CustomerEntity c")
    Integer findLastBusinessId();

    Optional<Object> findByPhoneNumber(@NotBlank String phoneNumber);
}