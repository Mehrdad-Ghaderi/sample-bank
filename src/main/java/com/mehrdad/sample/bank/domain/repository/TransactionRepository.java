package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("""
            select distinct t
            from TransactionEntity t
            join t.sender s
            join s.customer senderCustomer
            join senderCustomer.ownerUser senderOwner
            join t.receiver r
            join r.customer receiverCustomer
            join receiverCustomer.ownerUser receiverOwner
            where (senderOwner.username = :ownerUsername
                or receiverOwner.username = :ownerUsername)
              and (:accountNumber is null
                or s.number = :accountNumber
                or r.number = :accountNumber)
            """)
    Page<TransactionEntity> searchTransactionsByOwner(
            @Param("ownerUsername") String ownerUsername,
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );
}
