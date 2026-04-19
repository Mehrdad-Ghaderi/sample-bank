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
            where (:number is null or a.number = :number)
            """)
    Page<AccountEntity> searchAccounts(@Param("number") String number, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.id = :id")
    Optional<AccountEntity> findByIdForUpdate(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a from AccountEntity a
            where a.accountRole = :accountRole and a.currency = :currency
            """)
    Optional<AccountEntity> findByAccountRoleAndCurrencyForUpdate(AccountRole accountRole, Currency currency);
}
