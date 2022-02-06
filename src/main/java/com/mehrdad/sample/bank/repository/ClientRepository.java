package com.mehrdad.sample.bank.repository;

import com.mehrdad.sample.bank.model.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, String> {
}
