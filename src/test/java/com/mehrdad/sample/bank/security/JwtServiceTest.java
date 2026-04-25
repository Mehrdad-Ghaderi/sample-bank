package com.mehrdad.sample.bank.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Test
    void generateTokenShouldIncludeIssuerAudienceAndJti() {
        JwtService jwtService = new JwtService(
                jwtEncoder,
                3600,
                "https://auth.sample-bank.local",
                "sample-bank-api"
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user",
                "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(jwtEncoder.encode(org.mockito.ArgumentMatchers.any())).thenReturn(
                new Jwt(
                        "token-value",
                        Instant.parse("2026-04-25T00:00:00Z"),
                        Instant.parse("2026-04-25T01:00:00Z"),
                        java.util.Map.of("alg", "HS256"),
                        java.util.Map.of("sub", "user")
                )
        );

        jwtService.generateToken(authentication);

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals("user", claims.getSubject());
        assertEquals("https://auth.sample-bank.local", claims.getIssuer().toString());
        assertEquals(List.of("sample-bank-api"), claims.getAudience());
        assertEquals("ROLE_USER", claims.getClaim("scope"));
        assertNotNull(claims.getId());
        assertFalse(claims.getId().isBlank());
    }
}
