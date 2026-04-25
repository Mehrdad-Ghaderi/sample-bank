package com.mehrdad.sample.bank.security;

import com.mehrdad.sample.bank.security.entity.RevokedAccessTokenEntity;
import com.mehrdad.sample.bank.security.repository.RevokedAccessTokenRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class RevokedAccessTokenService {

    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final Clock clock;

    public RevokedAccessTokenService(RevokedAccessTokenRepository revokedAccessTokenRepository) {
        this(revokedAccessTokenRepository, Clock.systemUTC());
    }

    RevokedAccessTokenService(RevokedAccessTokenRepository revokedAccessTokenRepository, Clock clock) {
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.clock = clock;
    }

    public void revoke(Jwt jwt) {
        if (jwt.getId() == null || jwt.getExpiresAt() == null) {
            return;
        }

        deleteExpiredTokens();

        RevokedAccessTokenEntity revokedToken = new RevokedAccessTokenEntity();
        revokedToken.setJti(jwt.getId());
        revokedToken.setExpiresAt(jwt.getExpiresAt());
        revokedToken.setRevokedAt(Instant.now(clock));
        revokedAccessTokenRepository.save(revokedToken);
    }

    public boolean isRevoked(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }

        return revokedAccessTokenRepository.existsByJtiAndExpiresAtAfter(jti, Instant.now(clock));
    }

    private void deleteExpiredTokens() {
        revokedAccessTokenRepository.deleteByExpiresAtBefore(Instant.now(clock));
    }
}
