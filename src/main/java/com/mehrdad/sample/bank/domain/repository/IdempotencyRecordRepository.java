package com.mehrdad.sample.bank.domain.repository;

import com.mehrdad.sample.bank.domain.entity.IdempotencyRecordEntity;
import com.mehrdad.sample.bank.domain.entity.TransactionType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecordEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<IdempotencyRecordEntity> findByIdempotencyKeyAndCommandType(
            String idempotencyKey,
            TransactionType commandType
    );
}
