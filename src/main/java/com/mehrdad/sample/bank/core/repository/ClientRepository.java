package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.ClientEntity;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<ClientEntity, String> {
}
