package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.MoneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyRepository  extends JpaRepository<MoneyEntity, String> {
}
