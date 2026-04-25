package com.mehrdad.sample.bank.security.repository;

import com.mehrdad.sample.bank.security.entity.RevokedAccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessTokenEntity, String> {

    void deleteByExpiresAtBefore(Instant cutoff);
}
