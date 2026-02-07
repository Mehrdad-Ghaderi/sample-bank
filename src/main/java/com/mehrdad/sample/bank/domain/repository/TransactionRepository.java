package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query(value = """
    select tr.*
    from transaction_entity tr
    where tr.receiver_id = :accountId
       or tr.sender_id = :accountId
    order by tr.transaction_time desc
    limit :limit
""", nativeQuery = true)
    List<TransactionEntity> findLastTransactions(UUID accountNumber, int numberOfTransactions);



    @Query(value = """
    select tr.*
    from transaction_entity tr
    join account_entity a1 on tr.sender_id = a1.id
    join account_entity a2 on tr.receiver_id = a2.id
    where a1.number = :accountNumber
       or a2.number = :accountNumber
    order by tr.transaction_time desc
    limit :limit
""", nativeQuery = true)
    List<TransactionEntity> findLastTransactionsByAccountNumber(
            String accountNumber,
            int limit
    );

}
