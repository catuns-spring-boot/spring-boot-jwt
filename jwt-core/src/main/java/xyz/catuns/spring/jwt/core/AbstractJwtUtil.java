package xyz.catuns.spring.jwt.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import xyz.catuns.spring.jwt.core.exception.MissingSecretException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public abstract class AbstractJwtUtil<T> implements JwtUtil<T> {

    protected final String secret;

    public AbstractJwtUtil(String secret) throws MissingSecretException {
        if (secret == null || secret.isEmpty()) {
            throw new MissingSecretException();
        }
        this.secret = secret;
    }

    @Override
    public Claims getClaims(String token) {
        SecretKey secretKey = getSecretKey();
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
    }

    protected SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }


}
