package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    @Query(value = "select tr.* from transaction_entity tr " +
            "where tr.receiver_id = :account_id or tr.sender_id = :account_id\n" +
            "order by tr.transaction_time desc\n" +
            "limit :numberOfTransactions",
            nativeQuery = true)
    List<TransactionEntity> findLastTransactions(@Param("account_id")String accountNumber, @Param("numberOfTransactions")int numberOfTransactions);

}
