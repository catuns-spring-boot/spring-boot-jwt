package xyz.catuns.spring.jwt.core.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import xyz.catuns.spring.jwt.core.exception.JwtSecurityException;
import xyz.catuns.spring.jwt.core.exception.TokenValidationException;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.core.properties.JwtMetadata;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class SimpleTokenProvider<T> implements TokenProvider<T> {

    protected final String secret;
    protected final Duration expiration;
    protected final String issuer;
    /**
     * Set customizer prior to token generation
     */
    @Setter
    protected TokenGenerator<T> customizer;
    /**
     * Set customizer prior to token generation
     */
    @Setter
    private TokenValidator<T> validator;

    public SimpleTokenProvider(JwtMetadata jwtProperties) {
        this(jwtProperties, TokenGenerator.withDefaults(), TokenValidator.identity());
    }

    public SimpleTokenProvider(JwtMetadata jwtProperties, TokenGenerator<T> customizer, TokenValidator<T> validator) {
        this(
                jwtProperties.getSecret(),
                jwtProperties.getExpiration(),
                jwtProperties.getIssuer(),
                customizer,
                validator
        );
    }

    public SimpleTokenProvider(String secret, Duration expiration, String issuer) {
        this(secret, expiration, issuer, TokenGenerator.withDefaults(), TokenValidator.identity());
    }
    public SimpleTokenProvider(String secret, Duration expiration, String issuer, TokenGenerator<T> customizer, TokenValidator<T> validator) {
        if (secret == null || secret.isEmpty()) {
            throw new JwtSecurityException("missing secret");
        }
        this.secret = secret;
        this.expiration = expiration;
        this.issuer = issuer;
        this.customizer = customizer;
        this.validator = validator;
    }


    @Override
    public JwtToken generate(T claims) {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plus(this.expiration);
        JwtBuilder jwtBuilder = Jwts.builder()
                .issuer(this.issuer)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(this.getSecretKey());

        customizer.accept(jwtBuilder, claims);
        String token = jwtBuilder.compact();

        return new JwtToken(token, expiration, issuedAt);
    }

    @Override
    public T validate(String token) throws TokenValidationException {
        try {
            Claims claims = this.getClaims(token);
            return this.validator.apply(claims);
        } catch (Exception e) {
            throw new TokenValidationException(e);
        }
    }

    @Override
    public Claims getClaims(String token) {
        SecretKey secretKey = this.getSecretKey();
        return Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    protected SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }

}
