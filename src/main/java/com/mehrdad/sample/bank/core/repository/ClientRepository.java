package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, String> {
}
