package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.AccountRole;
import com.mehrdad.sample.bank.domain.entity.Currency;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByNumber(String number);

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
