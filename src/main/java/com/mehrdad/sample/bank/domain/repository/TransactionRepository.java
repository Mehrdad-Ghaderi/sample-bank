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
            select t
            from TransactionEntity t
            where (:accountNumber is null
                or t.sender.number = :accountNumber
                or t.receiver.number = :accountNumber)
            """)
    Page<TransactionEntity> searchTransactions(
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );
}
