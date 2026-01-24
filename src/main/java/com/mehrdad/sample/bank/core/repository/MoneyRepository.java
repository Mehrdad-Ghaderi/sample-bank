package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
public interface MoneyRepository  extends JpaRepository<MoneyEntity, UUID> {
}
