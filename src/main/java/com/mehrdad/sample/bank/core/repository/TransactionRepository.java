package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Mehrdad Ghaderi
 */
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    @Query(value = "select tr.* from transaction_entity tr " +
            "where tr.receiver_id = :accountNumber or tr.sender_id = :accountNumber\n" +
            "order by tr.transaction_time desc\n" +
            "limit :numberOfTransactions",
            nativeQuery = true)
    List<TransactionEntity> findLastTransactions(String accountNumber, int numberOfTransactions);

}
