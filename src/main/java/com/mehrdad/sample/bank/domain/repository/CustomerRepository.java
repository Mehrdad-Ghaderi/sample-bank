package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.AccountRole;
import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    @Query("select max(c.businessId) from CustomerEntity c")
    Integer findLastBusinessId();

    @Query("""
            select c
            from CustomerEntity c
            where c.ownerUsername = :ownerUsername
              and (:businessId is null or c.businessId = :businessId)
              and (:phoneNumber is null or c.phoneNumber = :phoneNumber)
            """)
    Page<CustomerEntity> searchCustomers(
            @Param("ownerUsername") String ownerUsername,
            @Param("businessId") Integer businessId,
            @Param("phoneNumber") String phoneNumber,
            Pageable pageable
    );

    Optional<CustomerEntity> findByPhoneNumber(@NotBlank String phoneNumber);

    boolean existsByPhoneNumber(@Pattern(regexp = "^\\+?[0-9]{10,15}$") String phoneNumber);

    @Query("""
            select distinct c
            from CustomerEntity c
            join c.accounts a
            where a.accountRole = :accountRole
            """)
    Optional<CustomerEntity> findByAccountRole(AccountRole accountRole);

}
