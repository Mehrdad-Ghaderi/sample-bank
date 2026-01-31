package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("""
        select a
        from AccountEntity a
        join fetch a.customer c
        where a.status = :accountStatus
          and c.status = :customerStatus
    """)
    List<AccountEntity> findActiveAccountsOfActiveCustomers(
            @Param("accountStatus") Status accountStatus,
            @Param("customerStatus") Status customerStatus
    );
}
