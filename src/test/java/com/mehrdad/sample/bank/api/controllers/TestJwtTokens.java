package com.mehrdad.sample.bank.api.controllers;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.UUID;

final class TestJwtTokens {

    private static final String SECRET = "sample-bank-local-development-secret-must-be-at-least-32-bytes";
    private static final String ISSUER = "https://auth.sample-bank.local";
    private static final String AUDIENCE = "sample-bank-api";

    private TestJwtTokens() {
    }

    static String bearerToken() {
        return bearerToken("user");
    }

    static String bearerToken(String subject) {
        return bearerToken(subject, "ROLE_USER");
    }

    static String adminBearerToken() {
        return bearerToken("admin", "ROLE_ADMIN");
    }

    static String bearerToken(String subject, String scope) {
        Instant now = Instant.now();
        return bearerToken(subject, scope, now, now.plusSeconds(3600), SECRET);
    }

    static String expiredBearerToken() {
        Instant now = Instant.now();
        return bearerToken("user", "ROLE_USER", now.minusSeconds(7200), now.minusSeconds(3600), SECRET, ISSUER, AUDIENCE);
    }

    static String bearerTokenSignedWithWrongSecret() {
        Instant now = Instant.now();
        return bearerToken(
                "user",
                "ROLE_USER",
                now,
                now.plusSeconds(3600),
                "wrong-sample-bank-local-development-secret-at-least-32-bytes",
                ISSUER,
                AUDIENCE
        );
    }

    static String bearerTokenWithWrongIssuer() {
        Instant now = Instant.now();
        return bearerToken("user", "ROLE_USER", now, now.plusSeconds(3600), SECRET, "https://wrong-issuer.local", AUDIENCE);
    }

    static String bearerTokenWithWrongAudience() {
        Instant now = Instant.now();
        return bearerToken("user", "ROLE_USER", now, now.plusSeconds(3600), SECRET, ISSUER, "other-api");
    }

    private static String bearerToken(String subject, String scope, Instant issuedAt, Instant expiresAt, String secret) {
        return bearerToken(subject, scope, issuedAt, expiresAt, secret, ISSUER, AUDIENCE);
    }

    private static String bearerToken(
            String subject,
            String scope,
            Instant issuedAt,
            Instant expiresAt,
            String secret,
            String issuer,
            String audience
    ) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .issuer(issuer)
                .audience(java.util.List.of(audience))
                .claim("scope", scope)
                .build();

        JwtEncoder jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(
                new SecretKeySpec(secret.getBytes(), "HmacSHA256")
        ));

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return "Bearer " + jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
