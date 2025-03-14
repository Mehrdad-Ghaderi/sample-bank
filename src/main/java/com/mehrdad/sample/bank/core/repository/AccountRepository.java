package com.mehrdad.sample.bank.core.repository;

import com.mehrdad.sample.bank.core.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Mehrdad Ghaderi
 */
public interface AccountRepository extends JpaRepository<AccountEntity, String> {

}
