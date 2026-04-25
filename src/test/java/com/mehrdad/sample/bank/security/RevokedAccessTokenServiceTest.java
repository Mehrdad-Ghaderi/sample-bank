package com.mehrdad.sample.bank.security;

import com.mehrdad.sample.bank.security.entity.RevokedAccessTokenEntity;
import com.mehrdad.sample.bank.security.repository.RevokedAccessTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevokedAccessTokenServiceTest {

    @Mock
    private RevokedAccessTokenRepository revokedAccessTokenRepository;

    @Test
    void revokeShouldPersistTokenIdentifierUntilExpiry() {
        Instant now = Instant.parse("2026-04-25T00:00:00Z");
        RevokedAccessTokenService service = new RevokedAccessTokenService(
                revokedAccessTokenRepository,
                Clock.fixed(now, ZoneOffset.UTC)
        );
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("jti", "11111111-1111-1111-1111-111111111111")
                .subject("user")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build();

        service.revoke(jwt);

        verify(revokedAccessTokenRepository).deleteByExpiresAtBefore(now);
        ArgumentCaptor<RevokedAccessTokenEntity> captor = ArgumentCaptor.forClass(RevokedAccessTokenEntity.class);
        verify(revokedAccessTokenRepository).save(captor.capture());

        RevokedAccessTokenEntity saved = captor.getValue();
        assertEquals("11111111-1111-1111-1111-111111111111", saved.getJti());
        assertEquals(now.plusSeconds(3600), saved.getExpiresAt());
        assertEquals(now, saved.getRevokedAt());
    }

    @Test
    void isRevokedShouldReturnFalseForBlankIdentifier() {
        RevokedAccessTokenService service = new RevokedAccessTokenService(
                revokedAccessTokenRepository,
                Clock.fixed(Instant.parse("2026-04-25T00:00:00Z"), ZoneOffset.UTC)
        );

        assertFalse(service.isRevoked(" "));
        verifyNoInteractions(revokedAccessTokenRepository);
    }

    @Test
    void isRevokedShouldCheckRepositoryAfterCleaningExpiredRows() {
        Instant now = Instant.parse("2026-04-25T00:00:00Z");
        RevokedAccessTokenService service = new RevokedAccessTokenService(
                revokedAccessTokenRepository,
                Clock.fixed(now, ZoneOffset.UTC)
        );
        when(revokedAccessTokenRepository.existsByJtiAndExpiresAtAfter("jti-123", now)).thenReturn(true);

        assertTrue(service.isRevoked("jti-123"));

        verify(revokedAccessTokenRepository).existsByJtiAndExpiresAtAfter("jti-123", now);
    }
}
