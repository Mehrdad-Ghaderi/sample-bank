package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.AccountRole;
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

    @Query("select max(c.businessId) from CustomerEntity c")
    Integer findLastBusinessId();

    Optional<CustomerEntity> findByPhoneNumber(@NotBlank String phoneNumber);

    boolean existsByPhoneNumber(@Pattern(regexp = "^\\+?[0-9]{10,15}$") String phoneNumber);

    Optional<CustomerEntity> findByBusinessId(Integer businessId);

    @Query("""
            select distinct c
            from CustomerEntity c
            join c.accounts a
            where a.accountRole = :accountRole
            """)
    Optional<CustomerEntity> findByAccountRole(AccountRole accountRole);

}
