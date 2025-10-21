package xyz.catuns.spring.jwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import xyz.catuns.spring.jwt.core.TokenProvider;
import xyz.catuns.spring.jwt.core.exception.MissingSecretException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public abstract class AbstractTokenProvider<T> implements TokenProvider<T> {

    protected final String secret;

    public AbstractTokenProvider(String secret) throws MissingSecretException {
        if (secret == null || secret.isEmpty()) {
            throw new MissingSecretException();
        }
        this.secret = secret;
    }

    @Override
    public Claims getClaims(String token) {
        SecretKey secretKey = this.getSecretKey();
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
    }

    @Override
    public boolean isExpired(String token) {
        Claims claims = this.getClaims(token);
        Date expiration = claims.getExpiration();
        return Instant.now().isAfter(expiration.toInstant());
    }

    protected SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }

}
