package xyz.catuns.spring.jwt.core.model;

import java.time.Instant;

public record JwtToken(
        String value,
        Instant expiration,
        Instant issuedAt,
        String subject
) {}
