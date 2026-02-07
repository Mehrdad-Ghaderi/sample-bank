package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    Optional<CustomerEntity> findByPhoneNumber(@NotBlank String phoneNumber);

    boolean existsByPhoneNumber(@Pattern(regexp = "^\\+?[0-9]{10,15}$") String phoneNumber);

    Optional<CustomerEntity> findByBusinessId(Integer businessId);

}