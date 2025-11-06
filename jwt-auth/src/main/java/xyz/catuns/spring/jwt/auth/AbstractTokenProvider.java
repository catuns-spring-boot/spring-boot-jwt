package xyz.catuns.spring.jwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import xyz.catuns.spring.jwt.core.TokenProvider;
import xyz.catuns.spring.jwt.core.exception.MissingSecretException;
import xyz.catuns.spring.jwt.core.model.JwtToken;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public abstract class AbstractTokenProvider<T> implements TokenProvider<T> {

    protected final String secret;
    protected final Duration expiration;

    /**
     * Set customizer prior to token generation
     */
    @Setter
    protected JwtCustomizer<T> customizer = JwtCustomizer.withDefaults();

    public AbstractTokenProvider(String secret, Duration expiration) throws MissingSecretException {
        if (secret == null || secret.isEmpty()) {
            throw new MissingSecretException();
        }
        this.secret = secret;
        this.expiration = expiration;
    }

    @Override
    public JwtToken generate(T claims) {
        Instant now = Instant.now();
        Instant expiration = now.plus(this.expiration);
        JwtBuilder jwtBuilder = Jwts.builder();
        customizer.customize(jwtBuilder, claims);
        String token = jwtBuilder.issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSecretKey())
                .compact();

        return new JwtToken(token, expiration, now);

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
