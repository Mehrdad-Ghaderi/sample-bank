package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.AccountRole;
import com.mehrdad.sample.bank.domain.entity.Currency;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByNumber(String number);

    @Query("""
            select a
            from AccountEntity a
            join a.customer c
            join c.ownerUser ownerUser
            where ownerUser.username = :ownerUsername
              and (:number is null or a.number = :number)
            """)
    Page<AccountEntity> searchAccountsByOwner(
            @Param("ownerUsername") String ownerUsername,
            @Param("number") String number,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.id = :id")
    Optional<AccountEntity> findByIdForUpdate(@Param("id") UUID id);

    @Query("""
            select count(a) > 0
            from AccountEntity a
            where a.id = :id and a.accountRole = :accountRole
            """)
    boolean existsByIdAndAccountRole(
            @Param("id") UUID id,
            @Param("accountRole") AccountRole accountRole
    );

    @Query("""
            select count(a) > 0
            from AccountEntity a
            join a.customer c
            join c.ownerUser ownerUser
            where a.id = :id
              and a.accountRole = :accountRole
              and ownerUser.username = :ownerUsername
            """)
    boolean existsByIdAndAccountRoleAndOwnerUsername(
            @Param("id") UUID id,
            @Param("accountRole") AccountRole accountRole,
            @Param("ownerUsername") String ownerUsername
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a from AccountEntity a
            where a.accountRole = :accountRole and a.currency = :currency
            """)
    Optional<AccountEntity> findByAccountRoleAndCurrencyForUpdate(
            @Param("accountRole") AccountRole accountRole,
            @Param("currency") Currency currency
    );
}
